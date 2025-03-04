package com.ahnis.journalai.journal.dto.response;

import java.time.Instant;
import java.time.ZonedDateTime;

public record JournalResponse(
        String id,
        String title,
        String content,
        Instant createdAt,
        Instant modifiedAt,
        String userId
) {
}
