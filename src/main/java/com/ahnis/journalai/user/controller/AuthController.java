package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.loginUser(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest dto) {
        var registeredUserAuthResponse = authService.registerUser(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUserAuthResponse);
    }


    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        passwordResetService.sendPasswordResetEmail(email);
        return "Password reset email sent.";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return "Password reset successfully.";
    }
}
