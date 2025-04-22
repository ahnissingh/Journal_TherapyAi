package com.ahnis.journalai.analysis.service;

import com.ahnis.journalai.analysis.dto.MoodReportApiResponse;
import com.ahnis.journalai.analysis.dto.MoodReportEmailResponse;
import com.ahnis.journalai.analysis.entity.MoodReportEntity;
import com.ahnis.journalai.analysis.exception.ReportNotFoundException;
import com.ahnis.journalai.analysis.mapper.ReportMapper;
import com.ahnis.journalai.analysis.repository.ReportRepository;
import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private JournalAnalysisService journalAnalysisService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportService reportService;

    private User testUser;
    private MoodReportEntity testReportEntity;
    private MoodReportEmailResponse testEmailResponse;
    private MoodReportApiResponse testApiResponse;
    private final String TEST_USER_ID = "user123";
    private final String TEST_REPORT_ID = "report123";

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username("testuser")
                .email("test@example.com")
                .preferences(new Preferences())
                .build();

        // Create test mood report email response
        Map<String, String> keyEmotions = new HashMap<>();
        keyEmotions.put("happiness", "70%");
        keyEmotions.put("sadness", "10%");
        keyEmotions.put("anxiety", "20%");

        testEmailResponse = new MoodReportEmailResponse(
                "Very positive mood with minimal sadness",
                keyEmotions,
                List.of("Your mood has improved", "You're handling stress better"),
                List.of("Keep up the good work", "Try new relaxation techniques"),
                "Every day is a new beginning."
        );

        // Create test mood report entity
        testReportEntity = MoodReportEntity.builder()
                .id(TEST_REPORT_ID)
                .userId(TEST_USER_ID)
                .reportDate(Instant.now())
                .moodSummary("Very positive mood with minimal sadness")
                .keyEmotions(keyEmotions)
                .insights(List.of("Your mood has improved", "You're handling stress better"))
                .recommendations(List.of("Keep up the good work", "Try new relaxation techniques"))
                .quote("Every day is a new beginning.")
                .createdAt(Instant.now())
                .build();

        // Create test mood report API response
        testApiResponse = new MoodReportApiResponse(
                TEST_REPORT_ID,
                Instant.now(),
                "Very positive mood with minimal sadness",
                keyEmotions,
                List.of("Your mood has improved", "You're handling stress better"),
                List.of("Keep up the good work", "Try new relaxation techniques"),
                "Every day is a new beginning.",
                Instant.now()
        );
    }

    @Test
    @DisplayName("Should send email report successfully")
    void sendReport_ShouldSendEmailReport() {
        // Given
        doNothing().when(notificationService).sendEmailReport(anyString(), any(MoodReportEmailResponse.class));

        // When
        reportService.sendReport(testUser, testEmailResponse);

        // Then
        verify(notificationService).sendEmailReport(testUser.getEmail(), testEmailResponse);
    }

    @Test
    @DisplayName("Should generate report successfully")
    void generateReport_ShouldGenerateAndSaveReport() {
        // Given
        Instant startDate = Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
        Instant endDate = Instant.now();

        when(journalAnalysisService.analyzeUserMood(anyString(), anyString(), any(Preferences.class), any(Instant.class), any(Instant.class)))
                .thenReturn(CompletableFuture.completedFuture(testEmailResponse));
        when(reportMapper.toMoodReportEntity(any(User.class), any(MoodReportEmailResponse.class))).thenReturn(testReportEntity);
        when(reportRepository.save(any(MoodReportEntity.class))).thenReturn(testReportEntity);
        doNothing().when(notificationService).sendEmailReport(anyString(), any(MoodReportEmailResponse.class));

        // When
        reportService.generateReport(testUser, startDate, endDate);

        // Then
        verify(journalAnalysisService).analyzeUserMood(testUser.getId(), testUser.getUsername(), testUser.getPreferences(), startDate, endDate);
        verify(reportMapper).toMoodReportEntity(testUser, testEmailResponse);
        verify(reportRepository).save(testReportEntity);
        verify(notificationService).sendEmailReport(testUser.getEmail(), testEmailResponse);
    }

    @Test
    @DisplayName("Should get all reports by user ID successfully")
    void getAllReportsByUserId_ShouldReturnPageOfReports() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MoodReportEntity> reportPage = new PageImpl<>(List.of(testReportEntity), pageable, 1);

        when(reportRepository.findByUserId(anyString(), any(Pageable.class))).thenReturn(reportPage);
        when(reportMapper.toApiResponse(any(MoodReportEntity.class))).thenReturn(testApiResponse);

        // When
        Page<MoodReportApiResponse> result = reportService.getAllReportsByUserId(TEST_USER_ID, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testApiResponse, result.getContent().get(0));
        verify(reportRepository).findByUserId(TEST_USER_ID, pageable);
        verify(reportMapper).toApiResponse(testReportEntity);
    }

    @Test
    @DisplayName("Should get report by ID successfully")
    void getReportById_ShouldReturnReport() {
        // Given
        when(reportRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Optional.of(testReportEntity));
        when(reportMapper.toApiResponse(any(MoodReportEntity.class))).thenReturn(testApiResponse);

        // When
        MoodReportApiResponse result = reportService.getReportById(TEST_USER_ID, TEST_REPORT_ID);

        // Then
        assertNotNull(result);
        assertEquals(testApiResponse, result);
        verify(reportRepository).findByIdAndUserId(TEST_REPORT_ID, TEST_USER_ID);
        verify(reportMapper).toApiResponse(testReportEntity);
    }

    @Test
    @DisplayName("Should throw exception when report not found by ID")
    void getReportById_ShouldThrowException_WhenReportNotFound() {
        // Given
        when(reportRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ReportNotFoundException.class, () ->
                reportService.getReportById(TEST_USER_ID, TEST_REPORT_ID)
        );
        verify(reportRepository).findByIdAndUserId(TEST_REPORT_ID, TEST_USER_ID);
        verify(reportMapper, never()).toApiResponse(any(MoodReportEntity.class));
    }

    @Test
    @DisplayName("Should get latest report by user ID successfully")
    void getLatestReportByUserId_ShouldReturnLatestReport() {
        // Given
        when(reportRepository.findFirstByUserIdOrderByReportDateDesc(anyString())).thenReturn(Optional.of(testReportEntity));
        when(reportMapper.toApiResponse(any(MoodReportEntity.class))).thenReturn(testApiResponse);

        // When
        MoodReportApiResponse result = reportService.getLatestReportByUserId(TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(testApiResponse, result);
        verify(reportRepository).findFirstByUserIdOrderByReportDateDesc(TEST_USER_ID);
        verify(reportMapper).toApiResponse(testReportEntity);
    }

    @Test
    @DisplayName("Should throw exception when no reports found for user")
    void getLatestReportByUserId_ShouldThrowException_WhenNoReportsFound() {
        // Given
        when(reportRepository.findFirstByUserIdOrderByReportDateDesc(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ReportNotFoundException.class, () ->
                reportService.getLatestReportByUserId(TEST_USER_ID)
        );
        verify(reportRepository).findFirstByUserIdOrderByReportDateDesc(TEST_USER_ID);
        verify(reportMapper, never()).toApiResponse(any(MoodReportEntity.class));
    }
}
