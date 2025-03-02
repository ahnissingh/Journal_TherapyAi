package com.ahnis.journalai.ai.analysis.dto;

import java.util.List;
import java.util.Map;

public record MoodReportResponse(
        String moodSummary,
        Map<String, String> keyEmotions,
        List<String> contextualInsights,
        List<String> recommendations
) {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Mood Summary
        builder.append("=== Mood Summary ===\n");
        builder.append(moodSummary).append("\n\n");

        // Key Emotions
        builder.append("=== Key Emotions ===\n");
        keyEmotions.forEach((emotion, percentage) ->
                builder.append("- ").append(emotion).append(": ").append(percentage).append("\n")
        );
        builder.append("\n");

        // Contextual Insights
        builder.append("=== Contextual Insights ===\n");
        contextualInsights.forEach(insight ->
                builder.append("- ").append(insight).append("\n")
        );
        builder.append("\n");

        // Recommendations
        builder.append("=== Recommendations ===\n");
        recommendations.forEach(recommendation ->
                builder.append("- ").append(recommendation).append("\n")
        );

        return builder.toString();
    }
}
