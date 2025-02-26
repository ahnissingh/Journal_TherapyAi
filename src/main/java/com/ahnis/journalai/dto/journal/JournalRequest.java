package com.ahnis.journalai.dto.journal;

import jakarta.validation.constraints.NotBlank;

public record JournalRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
