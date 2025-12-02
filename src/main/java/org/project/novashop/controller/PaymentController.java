package org.project.novashop.controller;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.payments.PaymentRequestDto;
import org.project.novashop.dto.payments.PaymentResponseDto;
import org.project.novashop.dto.payments.PaymentSummaryDto;
import org.project.novashop.service.PaymentService;
import org.project.novashop.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;
    private final PermissionService permissionService;

    public PaymentController(PaymentService paymentService, PermissionService permissionService) {
        this.paymentService = paymentService;
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> create(
            @Valid @RequestBody PaymentRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PaymentResponseDto> response = paymentService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> findById(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PaymentResponseDto> response = paymentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getPaymentsByCommande(
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<List<PaymentResponseDto>> response = paymentService.findByCommande(commandeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/commande/{commandeId}/summary")
    public ResponseEntity<ApiResponse<PaymentSummaryDto>> getPaymentSummary(
            @PathVariable Long commandeId,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PaymentSummaryDto> response = paymentService.getPaymentSummary(commandeId);
        return ResponseEntity.ok(response);
    }
}