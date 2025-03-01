package com.ahnis.journalai.ai.analysis.dto;

import java.util.List;
import java.util.Map;

public record MoodReport(
        String moodSummary,
        Map<String, String> keyEmotions,
        List<String> contextualInsights,
        List<String> recommendations
) {
}
