package org.project. novashop.service;

import org.project.novashop. dto.api.ApiResponse;
import org. project.novashop.dto. clients.ClientRequestDto;
import org.project.novashop. dto.clients.ClientResponseDto;
import org.project.novashop.dto.clients.ClientStatsDto;
import org.project.novashop.enums.CustomerTier;
import org.project.novashop.exception.DuplicateResourceException;
import org.project.novashop. exception.ResourceNotFoundException;
import org.project.novashop.mapper.ClientMapper;
import org. project.novashop.mapper. UserMapper;
import org.project.novashop.model.Client;
import org.project.novashop.model.User;
import org.project.novashop.repository.ClientRepository;
import org.project.novashop. repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction. annotation.Transactional;

import java.util.List;
import java.util.stream. Collectors;

@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;

    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         ClientMapper clientMapper,
                         UserMapper userMapper) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.clientMapper = clientMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public ApiResponse<ClientResponseDto> create(ClientRequestDto requestDto) {
        if (clientRepository.existsByTelephone(requestDto.getTelephone())) {
            throw new DuplicateResourceException("Client", "telephone", requestDto.getTelephone());
        }

        if (userRepository.existsByUsername(requestDto.getUser().getUsername())) {
            throw new DuplicateResourceException("User", "username", requestDto.getUser().getUsername());
        }

        User user = userMapper.toEntity(requestDto. getUser());
        User savedUser = userRepository.save(user);

        Client client = clientMapper.toEntity(requestDto);
        client.setUser(savedUser);

        Client savedClient = clientRepository.save(client);
        ClientResponseDto responseDto = clientMapper.toResponseDto(savedClient);

        return new ApiResponse<>("Client et User créés avec succès", responseDto);
    }

    public ApiResponse<List<ClientResponseDto>> findAll() {
        List<ClientResponseDto> clients = clientRepository. findAll()
                .stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
        return new ApiResponse<>("Liste des clients récupérée avec succès", clients);
    }

    public ApiResponse<ClientResponseDto> findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        ClientResponseDto responseDto = clientMapper.toResponseDto(client);
        return new ApiResponse<>("Client récupéré avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<ClientResponseDto> update(Long id, ClientRequestDto requestDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        if (! client.getTelephone().equals(requestDto.getTelephone()) &&
                clientRepository.existsByTelephone(requestDto. getTelephone())) {
            throw new DuplicateResourceException("Client", "telephone", requestDto.getTelephone());
        }

        clientMapper.updateEntityFromDto(requestDto, client);

        if (client.getUser() != null && requestDto.getUser() != null) {
            User user = client.getUser();

            if (!user. getUsername().equals(requestDto. getUser().getUsername()) &&
                    userRepository.existsByUsername(requestDto.getUser().getUsername())) {
                throw new DuplicateResourceException("User", "username", requestDto.getUser().getUsername());
            }

            userMapper.updateEntityFromDto(requestDto.getUser(), user);
            userRepository.save(user);
        }
        Client updatedClient = clientRepository.save(client);
        ClientResponseDto responseDto = clientMapper. toResponseDto(updatedClient);

        return new ApiResponse<>("Client et User mis à jour avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<Void> softDelete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        if (client.getUser() != null) {
            User user = client.getUser();
            user.setActive(false);
            userRepository. save(user);
        }

        return new ApiResponse<>("Client désactivé (User désactivé) avec succès");
    }

    @Transactional
    public ApiResponse<Void> hardDelete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        User user = client.getUser();

        clientRepository.delete(client);

        if (user != null) {
            userRepository.delete(user);
        }

        return new ApiResponse<>("Client et User supprimés définitivement avec succès");
    }

    @Transactional
    public ApiResponse<ClientResponseDto> assignUser(Long clientId, Long userId) {
        Client client = clientRepository. findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (clientRepository.existsByUserId(newUser.getId())) {
            throw new DuplicateResourceException("Client", "userId", newUser.getId().toString());
        }

        client.setUser(newUser);
        Client updatedClient = clientRepository.save(client);
        ClientResponseDto responseDto = clientMapper.toResponseDto(updatedClient);

        return new ApiResponse<>("User assigné au client avec succès", responseDto);
    }

    public ApiResponse<Long> count() {
        long count = clientRepository.count();
        return new ApiResponse<>("Nombre total de clients", count);
    }

    public ApiResponse<ClientStatsDto> getClientStats(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "userId", String.valueOf(userId)));

        CustomerTier niveauActuel = client.getNiveauFidelite();

        CustomerTier prochainNiveau = null;
        Integer commandesRestantes = null;
        Double montantRestant = null;

        int totalOrders = client.getTotalOrders();
        double totalSpent = client.getTotalSpent();

        switch (niveauActuel) {
            case BASIC:
                prochainNiveau = CustomerTier.SILVER;
                commandesRestantes = Math.max(0, 3 - totalOrders);
                montantRestant = Math.max(0, 1000 - totalSpent);
                break;
            case SILVER:
                prochainNiveau = CustomerTier.GOLD;
                commandesRestantes = Math.max(0, 10 - totalOrders);
                montantRestant = Math.max(0, 5000 - totalSpent);
                break;
            case GOLD:
                prochainNiveau = CustomerTier.PLATINUM;
                commandesRestantes = Math.max(0, 20 - totalOrders);
                montantRestant = Math.max(0, 15000 - totalSpent);
                break;
            case PLATINUM:
                prochainNiveau = null;
                commandesRestantes = null;
                montantRestant = null;
                break;
        }

        ClientStatsDto stats = ClientStatsDto.builder()
                .clientId(client.getId())
                .nom(client.getUser().getUsername())
                .niveauFidelite(niveauActuel)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .prochainNiveau(prochainNiveau)
                .commandesRestantes(commandesRestantes)
                .montantRestant(montantRestant)
                .build();

        return new ApiResponse<>("Statistiques récupérées avec succès", stats);
    }
}