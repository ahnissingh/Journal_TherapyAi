package com.ahnis.journalai.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorDetails(
        LocalDateTime timestamp,
        String message,
        Map<String, String> errors
) {
}
