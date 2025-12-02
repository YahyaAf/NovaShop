package org.project.novashop.controller;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.commandes.CommandeRequestDto;
import org.project.novashop.dto.commandes.CommandeResponseDto;
import org.project.novashop.enums.OrderStatus;
import org.project.novashop.exception.ResourceNotFoundException;
import org.project.novashop.model.Client;
import org.project.novashop.model.User;
import org.project.novashop.repository.ClientRepository;
import org.project.novashop.service.CommandeService;
import org.project.novashop.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@Validated
public class CommandeController {

    private final CommandeService commandeService;
    private final PermissionService permissionService;
    private final ClientRepository clientRepository;

    public CommandeController(CommandeService commandeService,
                              PermissionService permissionService,
                              ClientRepository clientRepository) {
        this.commandeService = commandeService;
        this.permissionService = permissionService;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommandeResponseDto>> create(
            @Valid @RequestBody CommandeRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<CommandeResponseDto> response = commandeService.create(requestDto.getClientId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<CommandeResponseDto>> confirm(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<CommandeResponseDto> response = commandeService.confirm(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CommandeResponseDto>> cancel(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<CommandeResponseDto> response = commandeService.cancel(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommandeResponseDto>> findById(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<CommandeResponseDto> response = commandeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mes-commandes")
    public ResponseEntity<ApiResponse<List<CommandeResponseDto>>> getMesCommandes(
            HttpServletRequest request) {
        User user = permissionService.getAuthenticatedUser(request);

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "userId", user.getId().toString()));

        ApiResponse<List<CommandeResponseDto>> response = commandeService.findByClient(client.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<CommandeResponseDto>>> getCommandesByClient(
            @PathVariable Long clientId,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<List<CommandeResponseDto>> response = commandeService.findByClient(clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CommandeResponseDto>>> getCommandesByStatus(
            @PathVariable OrderStatus status,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<List<CommandeResponseDto>> response = commandeService.findByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommandeResponseDto>>> getAllCommandes(
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<List<CommandeResponseDto>> response = commandeService.findAll();
        return ResponseEntity.ok(response);
    }
}