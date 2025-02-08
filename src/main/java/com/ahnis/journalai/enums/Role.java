package com.ahnis.journalai.enums;

import com.ahnis.journalai.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    USER, ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        return EnumUtils.fromString(Role.class, value);
    }
}
