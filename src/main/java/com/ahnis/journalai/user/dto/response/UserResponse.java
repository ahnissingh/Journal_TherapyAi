package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.enums.Role;

import java.time.Instant;
import java.util.Set;


public record UserResponse(
        String id,
        String username,
        String email,

        String firstName,
        String lastName,

        Set<Role> roles,
        PreferencesRequest preferences,
        Instant nextReportOn,
        Instant lastReportAt,// nullable (absent for new users)
        Instant createdAt,
        Instant updatedAt,
        int currentStreak,// Current consecutive days of journal writing
        int longestStreak, // Longest streak achieved
        Instant lastJournalEntryDate,// Date of the last journal entry
        Instant subscribedAt
) {
}
