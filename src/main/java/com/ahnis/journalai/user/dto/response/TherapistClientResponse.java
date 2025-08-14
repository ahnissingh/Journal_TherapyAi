package com.ahnis.journalai.user.dto.response;

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
) { }
