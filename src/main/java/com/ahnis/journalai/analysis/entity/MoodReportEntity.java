package com.ahnis.journalai.analysis.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Document(collection = "mood_reports")
@EnableMongoAuditing
public class MoodReportEntity {
    @Id
    private String id;
    @Indexed
    private String userId;
    private Instant reportDate;
    private String moodSummary;
    private Map<String, String> keyEmotions;
    private List<String> insights;
    private List<String> recommendations;
    private String quote;
    @CreatedDate
    private Instant createdAt;
}
