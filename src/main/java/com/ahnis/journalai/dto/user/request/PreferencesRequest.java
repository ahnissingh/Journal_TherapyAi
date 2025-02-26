package com.ahnis.journalai.dto.user.request;


import com.ahnis.journalai.enums.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//todo migrate Validation values to config
public record PreferencesRequest(

        @NotNull(message = "Therapy Frequency is required")
        TherapyFrequency therapyFrequency,
        @NotNull(message = "Language is required")
        Language language,
        @NotNull(message = "Theme preference is required")
        ThemePreference themePreference,
        @NotNull(message = "Therapist type is required")
        SupportStyle supportStyle,
        @Positive(message = "Age must be positive")
        @Min(value = 5, message = "Age must be at least ")
        @Max(value = 120, message = "Age must be less than or equal to 120")
        Integer age,
        @NotNull(message = "Gender is required")
        Gender gender
) {

}
