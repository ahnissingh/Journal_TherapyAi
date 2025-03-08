package com.ahnis.journalai.analysis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Map;
import java.util.List;

/**
 * This represents a user's report which will exposed via the rest api
 * to the ui client.
 *
 * @param reportDate
 * @param moodSummary
 * @param keyEmotions
 * @param insights
 * @param recommendations
 * @param quote
 * @param createdAt
 */
public record MoodReportApiResponse(
        String reportId,
        Instant reportDate,
        String moodSummary,
        Map<String, String> keyEmotions,
        List<String> insights,
        List<String> recommendations,
        String quote,

        Instant createdAt
) {
}
