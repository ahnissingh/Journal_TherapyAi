package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.user.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Language {
    ENGLISH, HINDI, PUNJABI, GERMAN, FRENCH, RUSSIAN;

    @JsonCreator
    public static Language fromString(String value) {
        return EnumUtils.fromString(Language.class, value);
    }
}
