package com.ahnis.journalai.notification.service;

import com.ahnis.journalai.analysis.dto.MoodReportEmailResponse;
import com.ahnis.journalai.notification.template.EmailTemplateService;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.repository.TherapistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SendGridEmailService sendGridEmailService;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private TherapistRepository therapistRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Therapist testTherapist;
    private MoodReportEmailResponse testMoodReport;
    private String testToken;
    private String testConcerningMessage;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .build();

        // Create test therapist
        testTherapist = Therapist.builder()
                .id("therapist123")
                .username("therapist")
                .email("therapist@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Create test mood report
        Map<String, String> keyEmotions = new HashMap<>();
        keyEmotions.put("happiness", "70%");
        keyEmotions.put("sadness", "10%");
        keyEmotions.put("anxiety", "20%");

        testMoodReport = new MoodReportEmailResponse(
                "Very positive mood with minimal sadness",
                keyEmotions,
                List.of("Your mood has improved", "You're handling stress better"),
                List.of("Keep up the good work", "Try new relaxation techniques"),
                "Every day is a new beginning."
        );

        // Create test token
        testToken = "test-token-123";

        // Create test concerning message
        testConcerningMessage = "I'm feeling very depressed and don't want to live anymore.";
    }

    @Test
    @DisplayName("Should send email report successfully")
    void sendEmailReport_ShouldSendEmail() {
        // Given
        String htmlContent = "<html><body>Test email content</body></html>";
        when(emailTemplateService.generateMoodReportEmail(any(MoodReportEmailResponse.class))).thenReturn(htmlContent);
        doNothing().when(sendGridEmailService).sendEmail(anyString(), anyString(), anyString());

        // When
        notificationService.sendEmailReport(testUser.getEmail(), testMoodReport);

        // Then
        verify(emailTemplateService).generateMoodReportEmail(testMoodReport);
        verify(sendGridEmailService).sendEmail(eq(testUser.getEmail()), anyString(), eq(htmlContent));
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void sendEmailPasswordReset_ShouldSendEmail() {
        // Given
        String htmlContent = "<html><body>Test password reset content</body></html>";
        when(emailTemplateService.generatePasswordResetEmail(anyString())).thenReturn(htmlContent);
        doNothing().when(sendGridEmailService).sendEmail(anyString(), anyString(), anyString());

        // When
        notificationService.sendEmailPasswordReset(testUser.getEmail(), testToken);

        // Then
        verify(emailTemplateService).generatePasswordResetEmail(testToken);
        verify(sendGridEmailService).sendEmail(eq(testUser.getEmail()), anyString(), eq(htmlContent));
    }

    @Test
    @DisplayName("Should send journal reminder email successfully")
    void sendEmailJournalReminder_ShouldSendEmail() {
        // Given
        String htmlContent = "<html><body>Test journal reminder content</body></html>";
        when(emailTemplateService.generateJournalReminderEmail()).thenReturn(htmlContent);
        doNothing().when(sendGridEmailService).sendEmail(anyString(), anyString(), anyString());

        // When
        notificationService.sendEmailJournalReminder(testUser.getEmail());

        // Then
        verify(emailTemplateService).generateJournalReminderEmail();
        verify(sendGridEmailService).sendEmail(eq(testUser.getEmail()), anyString(), eq(htmlContent));
    }

    @Test
    @DisplayName("Should send milestone notification email successfully")
    void sendMilestoneNotification_ShouldSendEmail() {
        // Given
        int streak = 7;
        String htmlContent = "<html><body>Test milestone notification content</body></html>";
        when(emailTemplateService.generateMilestoneNotificationEmail(anyString(), anyInt())).thenReturn(htmlContent);
        doNothing().when(sendGridEmailService).sendEmail(anyString(), anyString(), anyString());

        // When
        notificationService.sendMilestoneNotification(testUser, streak);

        // Then
        verify(emailTemplateService).generateMilestoneNotificationEmail(testUser.getUsername(), streak);
        verify(sendGridEmailService).sendEmail(eq(testUser.getEmail()), anyString(), eq(htmlContent));
    }

    @Test
    @DisplayName("Should send suicidal alert email successfully")
    void sendSuicidalAlert_ShouldSendEmail() {
        // Given
        String htmlContent = "<html><body>Test suicidal alert content</body></html>";
        when(therapistRepository.findById(anyString())).thenReturn(Optional.of(testTherapist));
        when(emailTemplateService.generateSuicidalAlertEmail(anyString(), anyString(), anyString(), any(LocalDateTime.class))).thenReturn(htmlContent);
        doNothing().when(sendGridEmailService).sendEmail(anyString(), anyString(), anyString());

        // When
        notificationService.sendSuicidalAlert(testUser.getUsername(), testTherapist.getId(), testConcerningMessage);

        // Then
        verify(therapistRepository).findById(testTherapist.getId());
        verify(emailTemplateService).generateSuicidalAlertEmail(eq(testTherapist.getFirstName()), eq(testUser.getUsername()), eq(testConcerningMessage), any(LocalDateTime.class));
        verify(sendGridEmailService).sendEmail(eq(testTherapist.getEmail()), anyString(), eq(htmlContent));
    }

    @Test
    @DisplayName("Should throw exception when therapist not found for suicidal alert")
    void sendSuicidalAlert_ShouldThrowException_WhenTherapistNotFound() {
        // Given
        when(therapistRepository.findById(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
                notificationService.sendSuicidalAlert(testUser.getUsername(), "nonexistent-therapist-id", testConcerningMessage)
        );
        verify(therapistRepository).findById("nonexistent-therapist-id");
        verify(emailTemplateService, never()).generateSuicidalAlertEmail(anyString(), anyString(), anyString(), any(LocalDateTime.class));
        verify(sendGridEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
