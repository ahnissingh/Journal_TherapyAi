package com.ahnis.journalai.dto.user.response;

import com.ahnis.journalai.dto.user.request.PreferencesRequest;
import com.ahnis.journalai.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;


public record UserResponse(
        String id,
        String username,
        String email,
        Set<Role> roles,
        PreferencesRequest preferences,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
