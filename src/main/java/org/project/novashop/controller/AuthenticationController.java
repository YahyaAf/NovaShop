package org.project. novashop.controller;

import org.project.novashop. dto.api.ApiResponse;
import org.project.novashop.dto.auth.LoginRequestDto;
import org.project.novashop.dto.auth.LoginResponseDto;
import org.project.novashop.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind. annotation.*;

import jakarta.servlet. http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto,
            HttpServletRequest request) {
        ApiResponse<LoginResponseDto> response = authenticationService.login(requestDto, request);
        return ResponseEntity. ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        ApiResponse<Void> response = authenticationService.logout(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponseDto>> getCurrentUser(HttpServletRequest request) {
        ApiResponse<LoginResponseDto> response = authenticationService.getCurrentUser(request);
        return ResponseEntity.ok(response);
    }
}