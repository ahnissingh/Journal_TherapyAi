package com.ahnis.journalai.user.enums;

import com.ahnis.journalai.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportFrequency {
    WEEKLY, BIWEEKLY, MONTHLY;

    @JsonCreator
    public static ReportFrequency fromString(String value) {
        return EnumUtils.fromString(ReportFrequency.class, value);
    }
}
