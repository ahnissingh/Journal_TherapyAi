package com.ahnis.journalai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Username should not be empty") String usernameOrEmail,
        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password should not be empty") String password
) {}
