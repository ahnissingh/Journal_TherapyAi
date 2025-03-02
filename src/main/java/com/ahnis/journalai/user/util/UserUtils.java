package com.ahnis.journalai.user.util;

import com.ahnis.journalai.user.enums.ReportFrequency;

import java.time.LocalDate;
import java.time.ZoneOffset;


public final class UserUtils {
    private UserUtils() {
        throw new UnsupportedOperationException("Cannot initialise Utility class");
    }

    public static LocalDate calculateNextReportOn(LocalDate currentDate, ReportFrequency reportFrequency) {
        LocalDate next = switch (reportFrequency) {
            case WEEKLY -> currentDate.plusDays(7);
            case BIWEEKLY -> currentDate.plusDays(14);
            case MONTHLY -> currentDate.plusMonths(1);
        };
        return next.atStartOfDay(ZoneOffset.UTC).toLocalDate();
    }
}
