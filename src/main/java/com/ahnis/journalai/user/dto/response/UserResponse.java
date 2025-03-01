package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


public record UserResponse(
        String id,
        String username,
        String email,
        Set<Role> roles,
        PreferencesRequest preferences,
        LocalDate nextReportOn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
