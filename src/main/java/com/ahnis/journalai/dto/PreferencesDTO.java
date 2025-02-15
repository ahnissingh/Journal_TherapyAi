package com.ahnis.journalai.dto;


import com.ahnis.journalai.enums.Language;
import com.ahnis.journalai.enums.ThemePreference;
import com.ahnis.journalai.enums.TherapistType;
import com.ahnis.journalai.enums.TherapyFrequency;
import jakarta.validation.constraints.NotNull;

public record PreferencesDTO(
        @NotNull(message = "Therapy Frequency is required")
        TherapyFrequency therapyFrequency,
        @NotNull(message = "Language is required")
        Language language,
        @NotNull
        ThemePreference themePreference,
        @NotNull
        TherapistType therapistType
) {
}
