package com.ahnis.journalai.dto.journal;

import java.time.LocalDateTime;

public record JournalResponse(
        String id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        String userId
) {}
