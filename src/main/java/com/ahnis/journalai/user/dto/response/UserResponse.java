package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.enums.Role;

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
