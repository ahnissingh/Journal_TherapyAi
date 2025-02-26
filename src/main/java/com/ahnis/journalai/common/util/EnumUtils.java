package com.ahnis.journalai.common.util;

import java.util.Arrays;

public final class EnumUtils {

    private EnumUtils() {}

    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Input value cannot be null or empty.");
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            //Throwing Exception with better message
            throw new IllegalArgumentException(
                    "Invalid value for enum '" + enumType.getSimpleName() + "': '" + value + "'. Expected one of: " +
                            Arrays.toString(enumType.getEnumConstants())
            );
        }
    }
}
