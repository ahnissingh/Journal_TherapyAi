package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TherapyFrequency {
    WEEKLY, BIWEEKLY, MONTHLY;

    @JsonCreator
    public static TherapyFrequency fromString(String value) {
        return EnumUtils.fromString(TherapyFrequency.class, value);
    }
}
