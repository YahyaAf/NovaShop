package org.project.novashop.service;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.users.UserRequestDto;
import org.project.novashop.dto.users.UserResponseDto;
import org.project.novashop.exception.DuplicateResourceException;
import org.project.novashop.exception.ResourceNotFoundException;
import org.project.novashop.mapper.UserMapper;
import org.project.novashop.model.User;
import org.project.novashop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public ApiResponse<UserResponseDto> create(UserRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", requestDto.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = userMapper.toEntity(requestDto, encodedPassword);

        User savedUser = userRepository.save(user);
        UserResponseDto responseDto = userMapper.toResponseDto(savedUser);

        return new ApiResponse<>("Utilisateur créé avec succès", responseDto);
    }

    public ApiResponse<UserResponseDto> findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        return new ApiResponse<>("Utilisateur récupéré avec succès", responseDto);
    }

    public ApiResponse<UserResponseDto> findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        return new ApiResponse<>("Utilisateur récupéré avec succès", responseDto);
    }

    public ApiResponse<List<UserResponseDto>> findAll() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return new ApiResponse<>("Liste des utilisateurs récupérée avec succès", users);
    }

    public ApiResponse<List<UserResponseDto>> findAllActive() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .filter(User::getActive)
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return new ApiResponse<>("Liste des utilisateurs actifs récupérée avec succès", users);
    }

    @Transactional
    public ApiResponse<UserResponseDto> update(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!user.getUsername().equals(requestDto.getUsername()) &&
                userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", requestDto.getUsername());
        }

        String encodedPassword = null;
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        }

        userMapper.updateEntityFromDto(requestDto, user, encodedPassword);
        User updatedUser = userRepository.save(user);
        UserResponseDto responseDto = userMapper.toResponseDto(updatedUser);

        return new ApiResponse<>("Utilisateur mis à jour avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<Void> softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setActive(false);
        userRepository.save(user);
        return new ApiResponse<>("Utilisateur désactivé avec succès");
    }

    @Transactional
    public ApiResponse<Void> hardDelete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
        return new ApiResponse<>("Utilisateur supprimé définitivement avec succès");
    }

    @Transactional
    public ApiResponse<UserResponseDto> activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setActive(true);
        User activatedUser = userRepository.save(user);
        UserResponseDto responseDto = userMapper.toResponseDto(activatedUser);

        return new ApiResponse<>("Utilisateur activé avec succès", responseDto);
    }

    public ApiResponse<Boolean> existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        return new ApiResponse<>("Vérification d'existence terminée", exists);
    }

    public ApiResponse<Long> count() {
        long count = userRepository.count();
        return new ApiResponse<>("Nombre total d'utilisateurs", count);
    }

    public ApiResponse<Long> countActive() {
        long count = userRepository.findAll()
                .stream()
                .filter(User::getActive)
                .count();
        return new ApiResponse<>("Nombre d'utilisateurs actifs", count);
    }
}