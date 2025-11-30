package org. project.novashop.service;

import org.project.novashop.dto.api.ApiResponse;
import org.project. novashop.dto.commandes.*;
import org.project.novashop.enums.CustomerTier;
import org.project.novashop.enums.OrderStatus;
import org.project.novashop.exception.ResourceNotFoundException;
import org. project.novashop.mapper.CommandeMapper;
import org.project.novashop.model.*;
import org.project.novashop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PromoRepository promoRepository;
    private final OrderItemRepository orderItemRepository;
    private final CommandeMapper commandeMapper;

    private static final Double TVA_RATE = 0.20; // 20%

    public CommandeService(CommandeRepository commandeRepository,
                           ClientRepository clientRepository,
                           ProductRepository productRepository,
                           PromoRepository promoRepository,
                           OrderItemRepository orderItemRepository,
                           CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.promoRepository = promoRepository;
        this.orderItemRepository = orderItemRepository;
        this.commandeMapper = commandeMapper;
    }

    @Transactional
    public ApiResponse<CommandeResponseDto> create(Long clientId, CommandeRequestDto requestDto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        List<OrderItem> orderItems = new ArrayList<>();
        double sousTotalHt = 0.0;

        for (OrderItemRequestDto itemDto : requestDto.getItems()) {
            Product product = productRepository.findByIdAndDeletedFalse(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemDto.getProductId()));

            // Vérifier stock
            if (product.getStock() < itemDto.getQuantite()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour " + product.getNom() +
                                ". Disponible: " + product.getStock() + ", Demandé: " + itemDto.getQuantite()
                );
            }

            // Créer OrderItem
            double totalLigne = product.getPrixUnitaire() * itemDto. getQuantite();
            OrderItem orderItem = OrderItem.builder()
                    . product(product)
                    .quantite(itemDto.getQuantite())
                    .prixUnitaire(product.getPrixUnitaire())
                    .totalLigne(totalLigne)
                    .build();

            orderItems.add(orderItem);
            sousTotalHt += totalLigne;
        }

        CommandeCalculationDto calculation = calculateRemises(client, sousTotalHt, requestDto.getCodePromo());

        Commande commande = Commande.builder()
                . numeroCommande(generateNumeroCommande())
                .dateCreation(LocalDateTime.now())
                .client(client)
                .sousTotalHt(calculation.getSousTotalHt())
                .montantRemise(calculation.getMontantRemiseTotal())
                .tva(calculation.getMontantTva())
                . totalTTC(calculation.getTotalTTC())
                .codePromo(requestDto.getCodePromo())
                .montantRestant(calculation.getTotalTTC())
                .statut(OrderStatus.PENDING)
                .build();

        for (OrderItem orderItem : orderItems) {
            orderItem.setCommande(commande);
        }
        commande.setOrderItems(orderItems);

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantite());
            productRepository.save(product);
        }

        if (requestDto.getCodePromo() != null && ! requestDto.getCodePromo(). isEmpty()) {
            promoRepository.findByCode(requestDto.getCodePromo()). ifPresent(promo -> {
                if (promo.getUsageCount() < promo.getMaxUsage()) {
                    promo.setUsageCount(promo.getUsageCount() + 1);
                    promoRepository.save(promo);
                    commande.setPromo(promo);
                }
            });
        }

        Commande savedCommande = commandeRepository.save(commande);

        CommandeResponseDto responseDto = commandeMapper.toResponseDto(savedCommande);
        responseDto.setMontantRemiseFidelite(calculation.getMontantRemiseFidelite());
        responseDto.setMontantRemisePromo(calculation.getMontantRemisePromo());
        responseDto.setMontantHtApresRemise(calculation.getMontantHtApresRemise());

        return new ApiResponse<>("Commande créée avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<CommandeResponseDto> confirm(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));

        if (commande.getStatut() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Seules les commandes en attente peuvent être confirmées");
        }

        if (commande.getMontantRestant() > 0) {
            throw new IllegalArgumentException(
                    "Impossible de confirmer la commande. Montant restant à payer: " +
                            commande.getMontantRestant() + " DH"
            );
        }

        Client client = commande.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent() + commande.getTotalTTC());
        updateClientTier(client);
        clientRepository.save(client);

        commande.setStatut(OrderStatus.CONFIRMED);
        Commande updatedCommande = commandeRepository.save(commande);

        CommandeResponseDto responseDto = commandeMapper.toResponseDto(updatedCommande);
        return new ApiResponse<>("Commande confirmée avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<CommandeResponseDto> cancel(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("Impossible d'annuler une commande déjà confirmée");
        }

        if (commande.getPayments() != null && !commande.getPayments().isEmpty()) {
            double totalPaye = commande.getPayments().stream()
                    .mapToDouble(Payment::getMontant)
                    .sum();

            if (totalPaye > 0) {
                throw new IllegalArgumentException(
                        "Impossible d'annuler la commande. Des paiements ont été réalisés."
                );
            }
        }

        for (OrderItem item : commande.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantite());
            productRepository.save(product);
        }

        commande.setStatut(OrderStatus.CANCELED);
        Commande updatedCommande = commandeRepository.save(commande);

        CommandeResponseDto responseDto = commandeMapper.toResponseDto(updatedCommande);
        return new ApiResponse<>("Commande annulée avec succès", responseDto);
    }

    public ApiResponse<CommandeResponseDto> findById(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));

        CommandeResponseDto responseDto = commandeMapper.toResponseDto(commande);
        return new ApiResponse<>("Commande récupérée avec succès", responseDto);
    }

    public ApiResponse<List<CommandeResponseDto>> findAll() {
        List<Commande> commandes = commandeRepository. findAll();
        List<CommandeResponseDto> responseDtos = commandes.stream()
                .map(commandeMapper::toResponseDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Toutes les commandes récupérées avec succès", responseDtos);
    }

    public ApiResponse<List<CommandeResponseDto>> findByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        List<Commande> commandes = commandeRepository.findByClientIdOrderByDateCreationDesc(clientId);
        List<CommandeResponseDto> responseDtos = commandes.stream()
                .map(commandeMapper::toResponseDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Commandes du client récupérées avec succès", responseDtos);
    }

    public ApiResponse<List<CommandeResponseDto>> findByStatus(OrderStatus statut) {
        List<Commande> commandes = commandeRepository.findByStatutOrderByDateCreationDesc(statut);
        List<CommandeResponseDto> responseDtos = commandes. stream()
                .map(commandeMapper::toResponseDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Commandes par statut récupérées avec succès", responseDtos);
    }

    private CommandeCalculationDto calculateRemises(Client client, Double sousTotalHt, String codePromo) {
        double montantRemiseFidelite = 0.0;
        double remiseFidelitePourcentage = 0.0;

        CustomerTier tier = client. getNiveauFidelite();
        switch (tier) {
            case SILVER:
                if (sousTotalHt >= 500) {
                    remiseFidelitePourcentage = 5.0;
                    montantRemiseFidelite = sousTotalHt * 0.05;
                }
                break;
            case GOLD:
                if (sousTotalHt >= 800) {
                    remiseFidelitePourcentage = 10.0;
                    montantRemiseFidelite = sousTotalHt * 0.10;
                }
                break;
            case PLATINUM:
                if (sousTotalHt >= 1200) {
                    remiseFidelitePourcentage = 15.0;
                    montantRemiseFidelite = sousTotalHt * 0.15;
                }
                break;
            default:
                break;
        }

        double montantRemisePromo = 0.0;
        double remisePromoPourcentage = 0.0;

        if (codePromo != null && !codePromo.isEmpty()) {
            var promoOpt = promoRepository.findByCode(codePromo);
            if (promoOpt. isPresent()) {
                Promo promo = promoOpt.get();
                if (promo.getUsageCount() < promo.getMaxUsage()) {
                    remisePromoPourcentage = 5.0;
                    montantRemisePromo = sousTotalHt * 0.05;
                }
            }
        }

        double montantRemiseTotal = montantRemiseFidelite + montantRemisePromo;
        double montantHtApresRemise = sousTotalHt - montantRemiseTotal;
        double montantTva = montantHtApresRemise * TVA_RATE;
        double totalTTC = montantHtApresRemise + montantTva;

        return CommandeCalculationDto.builder()
                . sousTotalHt(sousTotalHt)
                .remiseFidelitePourcentage(remiseFidelitePourcentage)
                .montantRemiseFidelite(montantRemiseFidelite)
                .remisePromoPourcentage(remisePromoPourcentage)
                .montantRemisePromo(montantRemisePromo)
                .montantRemiseTotal(montantRemiseTotal)
                .montantHtApresRemise(montantHtApresRemise)
                .tauxTva(TVA_RATE)
                .montantTva(montantTva)
                .totalTTC(totalTTC)
                .build();
    }

    private void updateClientTier(Client client) {
        int totalOrders = client.getTotalOrders();
        double totalSpent = client.getTotalSpent();

        if (totalOrders >= 20 || totalSpent >= 15000) {
            client.setNiveauFidelite(CustomerTier.PLATINUM);
        } else if (totalOrders >= 10 || totalSpent >= 5000) {
            client.setNiveauFidelite(CustomerTier. GOLD);
        } else if (totalOrders >= 3 || totalSpent >= 1000) {
            client.setNiveauFidelite(CustomerTier.SILVER);
        } else {
            client.setNiveauFidelite(CustomerTier.BASIC);
        }
    }

    private String generateNumeroCommande() {
        String prefix = "CMD-" + LocalDateTime.now().getYear() + "-";
        String uniqueId;
        do {
            uniqueId = prefix + String.format("%04d", (int) (Math.random() * 10000));
        } while (commandeRepository.existsByNumeroCommande(uniqueId));
        return uniqueId;
    }
}