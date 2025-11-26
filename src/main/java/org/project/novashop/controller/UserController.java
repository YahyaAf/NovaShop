package org.project.novashop.controller;

import jakarta.validation.Valid;
import org.project.novashop.dto.api.ApiResponse;
import org.project.novashop.dto.users.UserRequestDto;
import org.project.novashop.dto.users.UserResponseDto;
import org.project.novashop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> create(
            @Valid @RequestBody UserRequestDto requestDto) {

        ApiResponse<UserResponseDto> response = userService.create(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> findById(@PathVariable Long id) {
        ApiResponse<UserResponseDto> response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> findByUsername(
            @PathVariable String username) {

        ApiResponse<UserResponseDto> response = userService.findByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAll() {
        ApiResponse<List<UserResponseDto>> response = userService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAllActive() {
        ApiResponse<List<UserResponseDto>> response = userService.findAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {

        ApiResponse<UserResponseDto> response = userService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable Long id) {
        ApiResponse<Void> response = userService.softDelete(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDelete(@PathVariable Long id) {
        ApiResponse<Void> response = userService.hardDelete(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponseDto>> activate(@PathVariable Long id) {
        ApiResponse<UserResponseDto> response = userService.activate(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{username}")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(@PathVariable String username) {
        ApiResponse<Boolean> response = userService.existsByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count() {
        ApiResponse<Long> response = userService.count();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> countActive() {
        ApiResponse<Long> response = userService.countActive();
        return ResponseEntity.ok(response);
    }
}