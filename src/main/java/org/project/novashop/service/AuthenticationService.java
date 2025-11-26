package org.project.novashop.service;

import org.project.novashop.dto.api.ApiResponse;
import org.project. novashop.dto.auth. LoginRequestDto;
import org. project.novashop.dto. auth.LoginResponseDto;
import org.project.novashop. exception.ResourceNotFoundException;
import org. project.novashop.model.User;
import org.project.novashop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet. http.HttpSession;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    private final UserRepository userRepository;
    private static final String USER_SESSION_KEY = "LOGGED_USER";

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiResponse<LoginResponseDto> login(LoginRequestDto requestDto, HttpServletRequest request) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", requestDto.getUsername()));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        if (!user.getActive()) {
            throw new IllegalArgumentException("Compte désactivé");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user. getId());
        session.setMaxInactiveInterval(1800);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user. getRole())
                .sessionId(session.getId())
                . build();

        return new ApiResponse<>("Connexion réussie", responseDto);
    }

    @Transactional
    public ApiResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return new ApiResponse<>("Déconnexion réussie");
    }

    public ApiResponse<LoginResponseDto> getCurrentUser(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .sessionId(request. getSession(false).getId())
                .build();

        return new ApiResponse<>("Utilisateur actuel récupéré", responseDto);
    }


    public User getAuthenticatedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new IllegalArgumentException("Non authentifié.  Veuillez vous connecter.");
        }

        Long userId = (Long) session.getAttribute(USER_SESSION_KEY);

        if (userId == null) {
            throw new IllegalArgumentException("Non authentifié. Veuillez vous connecter.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.getActive()) {
            session.invalidate();
            throw new IllegalArgumentException("Compte désactivé");
        }

        return user;
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        try {
            getAuthenticatedUser(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getAuthenticatedUserId(HttpServletRequest request) {
        return getAuthenticatedUser(request).getId();
    }
}