package com.ahnis.journalai.analysis.repository;

import com.ahnis.journalai.analysis.entity.MoodReportEntity;
import com.ahnis.journalai.config.MongoTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    private MoodReportEntity testReport1;
    private MoodReportEntity testReport2;
    private final String TEST_USER_ID = "507f1f77bcf86cd799439011";
    private final String OTHER_USER_ID = "507f1f77bcf86cd799439012";

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        reportRepository.deleteAll();

        // Create test mood report entities
        Map<String, String> keyEmotions1 = new HashMap<>();
        keyEmotions1.put("happiness", "60%");
        keyEmotions1.put("sadness", "20%");
        keyEmotions1.put("anxiety", "20%");

        testReport1 = MoodReportEntity.builder()
                .userId(TEST_USER_ID)
                .reportDate(Instant.now().minus(7, ChronoUnit.DAYS))
                .moodSummary("Overall positive mood with some anxiety")
                .keyEmotions(keyEmotions1)
                .insights(List.of("You've been making progress", "Your anxiety has decreased"))
                .recommendations(List.of("Continue journaling", "Practice mindfulness"))
                .quote("The only way out is through.")
                .createdAt(Instant.now().minus(7, ChronoUnit.DAYS))
                .build();

        Map<String, String> keyEmotions2 = new HashMap<>();
        keyEmotions2.put("happiness", "70%");
        keyEmotions2.put("sadness", "10%");
        keyEmotions2.put("anxiety", "20%");

        testReport2 = MoodReportEntity.builder()
                .userId(TEST_USER_ID)
                .reportDate(Instant.now())
                .moodSummary("Very positive mood with minimal sadness")
                .keyEmotions(keyEmotions2)
                .insights(List.of("Your mood has improved", "You're handling stress better"))
                .recommendations(List.of("Keep up the good work", "Try new relaxation techniques"))
                .quote("Every day is a new beginning.")
                .createdAt(Instant.now())
                .build();

        // Save the test reports
        testReport1 = reportRepository.save(testReport1);
        testReport2 = reportRepository.save(testReport2);
    }

    @AfterEach
    void tearDown() {
        reportRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find reports by userId with pagination")
    void findByUserId_ShouldReturnReports() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Page<MoodReportEntity> result = reportRepository.findByUserId(TEST_USER_ID, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        // Verify sorting (newest first)
        assertEquals(testReport2.getId(), result.getContent().get(0).getId());
        assertEquals(testReport1.getId(), result.getContent().get(1).getId());
    }

    @Test
    @DisplayName("Should return empty page when no reports found for userId")
    void findByUserId_ShouldReturnEmptyPage_WhenNoReportsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MoodReportEntity> result = reportRepository.findByUserId(OTHER_USER_ID, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should find report by id and userId")
    void findByIdAndUserId_ShouldReturnReport() {
        // When
        Optional<MoodReportEntity> result = reportRepository.findByIdAndUserId(testReport1.getId(), TEST_USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testReport1.getId(), result.get().getId());
        assertEquals(TEST_USER_ID, result.get().getUserId());
    }

    @Test
    @DisplayName("Should return empty when report not found by id and userId")
    void findByIdAndUserId_ShouldReturnEmpty_WhenReportNotFound() {
        // When
        Optional<MoodReportEntity> result = reportRepository.findByIdAndUserId(testReport1.getId(), OTHER_USER_ID);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find the most recent report for a user")
    void findFirstByUserIdOrderByReportDateDesc_ShouldReturnMostRecentReport() {
        // When
        Optional<MoodReportEntity> result = reportRepository.findFirstByUserIdOrderByReportDateDesc(TEST_USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testReport2.getId(), result.get().getId()); // testReport2 has the most recent reportDate
    }

    @Test
    @DisplayName("Should return empty when no reports found for user")
    void findFirstByUserIdOrderByReportDateDesc_ShouldReturnEmpty_WhenNoReportsFound() {
        // When
        Optional<MoodReportEntity> result = reportRepository.findFirstByUserIdOrderByReportDateDesc(OTHER_USER_ID);

        // Then
        assertTrue(result.isEmpty());
    }
}
