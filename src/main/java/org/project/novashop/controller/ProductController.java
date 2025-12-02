package org.project.novashop.controller;

import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.products.ProductRequestDto;
import org.project.novashop.dto.products.ProductResponseDto;
import org.project.novashop.service.PermissionService;
import org.project.novashop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService productService;
    private final PermissionService permissionService;

    public ProductController(ProductService productService, PermissionService permissionService) {
        this.productService = productService;
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
            @Valid @RequestBody ProductRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<ProductResponseDto> response = productService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<ProductResponseDto> response = productService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<Void> response = productService.softDelete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> findAll(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<ProductResponseDto>> response = productService.findAllWithFilters(
                nom, minPrice, maxPrice, inStock, pageable
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> findById(
            @PathVariable Long id,
            HttpServletRequest request) {
        permissionService.requireAdmin(request);

        ApiResponse<ProductResponseDto> response = productService.findById(id);
        return ResponseEntity.ok(response);
    }
}