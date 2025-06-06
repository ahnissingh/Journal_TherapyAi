package com.ahnis.journalai.journal.dto.response;

import java.time.Instant;

public record JournalResponse(
        String id,
        String title,
        String content,

        Instant createdAt,

        Instant modifiedAt,
        String userId
) {

}
