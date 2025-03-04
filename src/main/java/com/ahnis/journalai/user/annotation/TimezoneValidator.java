package com.ahnis.journalai.user.annotation;

import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;

public class TimezoneValidator implements ConstraintValidator<ValidTimezone, String> {

    @Override
    public void initialize(ValidTimezone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String timezone, ConstraintValidatorContext context) {
        if (timezone == null) {
            return false; // Timezone is required
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            ZoneId.of(timezone); // Throws exception if timezone is invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
