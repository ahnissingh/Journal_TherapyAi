package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.entity.User;

import java.time.Instant;

public record TherapistClientResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        Instant subscribedAt,
        Instant lastJournalDate,
        int streakCount
) {
    public static TherapistClientResponse fromUser(User user) {
        return new TherapistClientResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getSubscribedAt(), // Add this field to User entity
                user.getLastJournalEntryDate(),
                user.getCurrentStreak()
        );
    }
}
