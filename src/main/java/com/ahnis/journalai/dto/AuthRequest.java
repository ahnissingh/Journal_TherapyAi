package com.ahnis.journalai.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) {}
