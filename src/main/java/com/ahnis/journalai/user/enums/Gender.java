package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.user.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER;

    @JsonCreator
    public static Gender fromString(String value) {
        return EnumUtils.fromString(Gender.class, value);
    }
}
