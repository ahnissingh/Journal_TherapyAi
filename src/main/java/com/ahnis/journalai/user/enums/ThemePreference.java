package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.user.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum ThemePreference {
    LIGHT, DARK;
    @JsonCreator
    public static ThemePreference fromString(String value) {
        return EnumUtils.fromString(ThemePreference.class, value);
    }
}
