package org.project.novashop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.project.novashop.enums.UserRole;
import org.project.novashop.exception.AccessDeniedException;
import org.project.novashop.model.User;
import org.project.novashop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public PermissionService(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        return authenticationService.getAuthenticatedUser(request);
    }

    public void requireAdmin(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Accès interdit. Privilèges ADMIN requis.");
        }
    }

    public void requireClient(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        if (user.getRole() != UserRole.CLIENT) {
            throw new AccessDeniedException("Accès interdit. Espace client uniquement.");
        }
    }

    public void requireClientOwnershipOrAdmin(HttpServletRequest request, Long resourceClientId) {
        User user = getAuthenticatedUser(request);

        if (user.getRole() == UserRole.ADMIN) return;

        if (user.getRole() == UserRole.CLIENT) {
            Long clientId = user.getClient() != null ? user.getClient().getId() : null;
            if (clientId == null || !clientId.equals(resourceClientId)) {
                throw new AccessDeniedException("Accès interdit. Vous ne pouvez accéder qu'à vos propres données.");
            }
        }
    }
}