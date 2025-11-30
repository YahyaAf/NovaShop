package org.project.novashop.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.auth.LoginResponseDto;
import org.project.novashop. dto.clients.ClientRequestDto;
import org.project.novashop.dto.clients.ClientResponseDto;
import org.project.novashop.dto.clients.ClientStatsDto;
import org.project.novashop.model.User;
import org.project.novashop.service.AuthenticationService;
import org.project. novashop.service.ClientService;
import org.springframework.http.HttpStatus;
import org. springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind. annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Validated
public class ClientController {

    private final ClientService clientService;
    private final AuthenticationService authenticationService;

    public ClientController(ClientService clientService,AuthenticationService authenticationService) {
        this.clientService = clientService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponseDto>> create(@Valid @RequestBody ClientRequestDto requestDto) {
        ApiResponse<ClientResponseDto> response = clientService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponseDto>>> findAll() {
        ApiResponse<List<ClientResponseDto>> response = clientService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> findById(@PathVariable Long id) {
        ApiResponse<ClientResponseDto> response = clientService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto requestDto) {
        ApiResponse<ClientResponseDto> response = clientService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long id) {
        ApiResponse<Void> response = clientService.softDelete(id);
        return ResponseEntity. ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> hardDelete(@PathVariable Long id) {
        ApiResponse<Void> response = clientService.hardDelete(id);
        return ResponseEntity. ok(response);
    }

    @PatchMapping("/{clientId}/assign-user/{userId}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> assignUser(
            @PathVariable Long clientId,
            @PathVariable Long userId) {
        ApiResponse<ClientResponseDto> response = clientService.assignUser(clientId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count() {
        ApiResponse<Long> response = clientService.count();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<ClientStatsDto>> getMyClientStats(HttpServletRequest request) {
        ApiResponse<LoginResponseDto> user = authenticationService.getCurrentUser(request);
        ApiResponse<ClientStatsDto> response = clientService.getClientStats(user.getData().getUserId());
        return ResponseEntity.ok(response);
    }
}