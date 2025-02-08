package com.ahnis.journalai.dto;

import com.ahnis.journalai.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;


public record UserResponseDTO(
        String id,
        String username,
        String email,
        Set<Role> roles,
        PreferencesDTO preferences,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
