package com.ahnis.journalai.analysis.scheduler;

import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportScheduler reportScheduler;

    private User testUser1;
    private User testUser2;
    private List<User> testUsers;
    private Instant today;
    private Instant tomorrow;

    @BeforeEach
    void setUp() {
        // Set up test dates
        LocalDate todayDate = LocalDate.now(ZoneOffset.UTC);
        today = todayDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        tomorrow = todayDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        // Create test preferences
        Preferences preferences = new Preferences();
        preferences.setReportFrequency(ReportFrequency.WEEKLY);

        // Create test users
        testUser1 = User.builder()
                .id("user1")
                .username("user1")
                .email("user1@example.com")
                .preferences(preferences)
                .nextReportOn(today)
                .lastReportAt(today.minus(7, ChronoUnit.DAYS))
                .createdAt(today.minus(30, ChronoUnit.DAYS))
                .build();

        testUser2 = User.builder()
                .id("user2")
                .username("user2")
                .email("user2@example.com")
                .preferences(preferences)
                .nextReportOn(today)
                .lastReportAt(null) // New user with no previous report
                .createdAt(today.minus(1, ChronoUnit.DAYS))
                .build();

        testUsers = List.of(testUser1, testUser2);
    }

    @Test
    @DisplayName("Should generate reports for users with reports due today")
    void checkForReports_ShouldGenerateReportsForUsersDueToday() {
        // Given
        when(userRepository.findByNextReportOn(any(Instant.class), any(Instant.class))).thenReturn(testUsers);
        doNothing().when(reportService).generateReport(any(User.class), any(Instant.class), any(Instant.class));
        doNothing().when(userRepository).updateLastReportAtById(anyString(), any(Instant.class));
        doNothing().when(userRepository).updateNextReportOnById(anyString(), any(Instant.class));

        // When
        reportScheduler.checkForReports();

        // Then
        verify(userRepository).findByNextReportOn(any(Instant.class), any(Instant.class));

        // Verify report generation for user1 (existing user with previous report)
        verify(reportService).generateReport(eq(testUser1), eq(testUser1.getLastReportAt()), eq(testUser1.getNextReportOn()));
        verify(userRepository).updateLastReportAtById(eq(testUser1.getId()), eq(testUser1.getNextReportOn()));
        verify(userRepository).updateNextReportOnById(eq(testUser1.getId()), any(Instant.class));

        // Verify report generation for user2 (new user with no previous report)
        verify(reportService).generateReport(eq(testUser2), eq(testUser2.getCreatedAt()), eq(testUser2.getNextReportOn()));
        verify(userRepository).updateLastReportAtById(eq(testUser2.getId()), eq(testUser2.getNextReportOn()));
        verify(userRepository).updateNextReportOnById(eq(testUser2.getId()), any(Instant.class));
    }

    @Test
    @DisplayName("Should not generate reports when no users have reports due today")
    void checkForReports_ShouldNotGenerateReports_WhenNoUsersDueToday() {
        // Given
        when(userRepository.findByNextReportOn(any(Instant.class), any(Instant.class))).thenReturn(List.of());

        // When
        reportScheduler.checkForReports();

        // Then
        verify(userRepository).findByNextReportOn(any(Instant.class), any(Instant.class));
        verify(reportService, never()).generateReport(any(User.class), any(Instant.class), any(Instant.class));
        verify(userRepository, never()).updateLastReportAtById(anyString(), any(Instant.class));
        verify(userRepository, never()).updateNextReportOnById(anyString(), any(Instant.class));
    }

    @Test
    @DisplayName("Should handle exceptions when generating reports")
    void checkForReports_ShouldHandleExceptions() {
        // Given
        when(userRepository.findByNextReportOn(any(Instant.class), any(Instant.class))).thenReturn(testUsers);
        doThrow(new RuntimeException("Test exception")).when(reportService).generateReport(eq(testUser1), any(Instant.class), any(Instant.class));
        doNothing().when(reportService).generateReport(eq(testUser2), any(Instant.class), any(Instant.class));
        doNothing().when(userRepository).updateLastReportAtById(anyString(), any(Instant.class));
        doNothing().when(userRepository).updateNextReportOnById(anyString(), any(Instant.class));

        // When
        reportScheduler.checkForReports();

        // Then
        verify(userRepository).findByNextReportOn(any(Instant.class), any(Instant.class));

        // Verify report generation attempt for user1 (which throws exception)
        verify(reportService).generateReport(eq(testUser1), any(Instant.class), any(Instant.class));
        verify(userRepository, never()).updateLastReportAtById(eq(testUser1.getId()), any(Instant.class));
        verify(userRepository, never()).updateNextReportOnById(eq(testUser1.getId()), any(Instant.class));

        // Verify report generation for user2 (which succeeds)
        verify(reportService).generateReport(eq(testUser2), any(Instant.class), any(Instant.class));
        verify(userRepository).updateLastReportAtById(eq(testUser2.getId()), any(Instant.class));
        verify(userRepository).updateNextReportOnById(eq(testUser2.getId()), any(Instant.class));
    }
}
