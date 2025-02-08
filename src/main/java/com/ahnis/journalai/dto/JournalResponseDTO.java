package com.ahnis.journalai.dto;

import java.time.LocalDateTime;

public record JournalResponseDTO(
        String id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        String userId
) {}
