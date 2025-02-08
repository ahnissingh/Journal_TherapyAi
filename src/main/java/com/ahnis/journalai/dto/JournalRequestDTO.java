package com.ahnis.journalai.dto;

import jakarta.validation.constraints.NotBlank;

public record JournalRequestDTO(
        @NotBlank String title,
        @NotBlank String content
) {
}
