package com.ahnis.journalai.ai.analysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Indexed(unique = true)
    private String userId;
    private Instant reportDate;
    private String moodSummary;
    private Map<String, String> keyEmotions;
    private List<String> insights;
    private List<String> recommendations;
    private String quote;
    @CreatedDate
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;
}
