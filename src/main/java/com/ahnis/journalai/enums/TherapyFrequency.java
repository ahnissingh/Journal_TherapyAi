package com.ahnis.journalai.enums;

import com.ahnis.journalai.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TherapyFrequency {
    WEEKLY, BIWEEKLY, MONTHLY;

    @JsonCreator
    public static TherapyFrequency fromString(String value) {
        return EnumUtils.fromString(TherapyFrequency.class, value);
    }
}
