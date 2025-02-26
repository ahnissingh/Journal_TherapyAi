package com.ahnis.journalai.user.exception;

import java.util.Arrays;

public class InvalidEnumException extends IllegalArgumentException {
    public InvalidEnumException(String value, Class<? extends Enum<?>> enumClass) {
        super("Invalid value '" + value + "' for enum " + enumClass.getSimpleName() + ". Expected one of: " + Arrays.toString(enumClass.getEnumConstants()));
    }
}
