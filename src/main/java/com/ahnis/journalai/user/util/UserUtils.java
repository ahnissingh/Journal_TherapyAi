package com.ahnis.journalai.user.util;

import com.ahnis.journalai.user.enums.ReportFrequency;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


public final class UserUtils {
    private UserUtils() {
        throw new UnsupportedOperationException("Cannot initialise Utility class");
    }

    public static Instant calculateNextReportOn(Instant currentDate, ReportFrequency reportFrequency) {
        return switch (reportFrequency) {
            case DAILY -> currentDate.plus(1, ChronoUnit.DAYS);
            case WEEKLY -> currentDate.plus(7, ChronoUnit.DAYS);
            case BIWEEKLY -> currentDate.plus(14, ChronoUnit.DAYS);
            case MONTHLY -> {
                // Convert Instant to ZonedDateTime to handle months
                ZonedDateTime zonedDateTime = currentDate.atZone(ZoneOffset.UTC);
                // Add 1 month
                ZonedDateTime updatedZonedDateTime = zonedDateTime.plusMonths(1);
                // Convert back to Instant
                yield updatedZonedDateTime.toInstant();
            }
        };
    }
}
