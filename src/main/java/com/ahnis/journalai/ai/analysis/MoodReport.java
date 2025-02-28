package com.ahnis.journalai.ai.analysis;

import java.util.List;
import java.util.Map;

public record MoodReport(
        String moodSummary,
        Map<String, Integer> keyEmotions,
        List<String> contextualInsights,
        List<String> recommendations
) {}
