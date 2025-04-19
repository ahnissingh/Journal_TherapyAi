package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.service.AuthService;
import com.ahnis.journalai.user.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        var authResponse = authService.loginUser(authRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Login successful", authResponse));
    }

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        var registeredUserAuthResponse = authService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "User registered successfully", registeredUserAuthResponse));
    }

    @PostMapping("/register/therapist")
    public ResponseEntity<ApiResponse<AuthResponse>> registerTherapist(@Valid @RequestBody TherapistRegistrationRequest request) {
        var therapistAuthResponse = authService.registerTherapist(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Therapist registered successfully", therapistAuthResponse));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String email) {
        passwordResetService.sendPasswordResetEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully."));
    }
}
