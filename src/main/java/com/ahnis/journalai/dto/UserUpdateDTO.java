package com.ahnis.journalai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// UserUpdateDTO.java
public record UserUpdateDTO(
        @Email(message = "Invalid email format")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Valid
        PreferencesDTO preferences
) {}

