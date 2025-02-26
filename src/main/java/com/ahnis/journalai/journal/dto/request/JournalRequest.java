package com.ahnis.journalai.journal.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JournalRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
