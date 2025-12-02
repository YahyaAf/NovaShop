package org.project.novashop.controller;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.promos.PromoRequestDto;
import org.project.novashop.dto.promos.PromoResponseDto;
import org.project.novashop.service.PromoService;
import org.project.novashop.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/promos")
@Validated
public class PromoController {

    private final PromoService promoService;
    private final PermissionService permissionService;

    public PromoController(PromoService promoService, PermissionService permissionService) {
        this.promoService = promoService;
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromoResponseDto>> create(
            @Valid @RequestBody PromoRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PromoResponseDto> response = promoService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromoResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PromoRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PromoResponseDto> response = promoService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromoResponseDto>> findById(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PromoResponseDto> response = promoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromoResponseDto>>> findAll(HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<List<PromoResponseDto>> response = promoService.findAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<Void> response = promoService.delete(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<PromoResponseDto>> apply(
            @RequestParam String code,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PromoResponseDto> response = promoService.validateAndApply(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<PromoResponseDto>> check(
            @RequestParam String code,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<PromoResponseDto> response = promoService.checkValidity(code);
        return ResponseEntity.ok(response);
    }
}