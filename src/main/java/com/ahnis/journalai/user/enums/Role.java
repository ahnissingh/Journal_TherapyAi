package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    USER, ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        return EnumUtils.fromString(Role.class, value);
    }
}
