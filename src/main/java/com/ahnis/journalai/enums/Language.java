package com.ahnis.journalai.enums;

import com.ahnis.journalai.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Language {
    ENGLISH, HINDI;

    @JsonCreator
    public static Language fromString(String value) {
        return EnumUtils.fromString(Language.class,value);
    }
}
