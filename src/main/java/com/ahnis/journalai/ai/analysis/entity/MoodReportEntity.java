package com.ahnis.journalai.ai.analysis.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Document(collection = "mood_reports")
public class MoodReportEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private LocalDate reportDate;


    private String moodSummary;
    private Map<String, String> keyEmotions;
    private List<String> contextualInsights;
    private List<String> recommendations;
}
