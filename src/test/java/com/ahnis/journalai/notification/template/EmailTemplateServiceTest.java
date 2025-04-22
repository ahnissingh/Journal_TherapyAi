package com.ahnis.journalai.notification.template;

import com.ahnis.journalai.analysis.dto.MoodReportEmailResponse;
import com.ahnis.journalai.common.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailTemplateServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    private MoodReportEmailResponse testMoodReport;
    private String testToken;
    private String testBaseUrl;
    private String testPasswordResetUrl;
    private String testHtmlContent;

    @BeforeEach
    void setUp() {
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

        // Create test URLs
        testBaseUrl = "http://example.com";
        testPasswordResetUrl = "/reset-password";

        // Create test HTML content
        testHtmlContent = "<html><body>Test email content</body></html>";

        // Set up TemplateEngine mock for all tests
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(testHtmlContent);
    }

    @Test
    @DisplayName("Should generate mood report email successfully")
    void generateMoodReportEmail_ShouldGenerateEmail() {
        // When
        String result = emailTemplateService.generateMoodReportEmail(testMoodReport);

        // Then
        assertEquals(testHtmlContent, result);
        verify(templateEngine).process(eq("email/mood-report-email"), any(Context.class));
    }

    @Test
    @DisplayName("Should generate password reset email successfully")
    void generatePasswordResetEmail_ShouldGenerateEmail() {
        // Set up AppProperties mock specifically for this test
        when(appProperties.getBaseUrl()).thenReturn(testBaseUrl);
        when(appProperties.getPasswordResetUrl()).thenReturn(testPasswordResetUrl);

        // When
        String result = emailTemplateService.generatePasswordResetEmail(testToken);

        // Then
        assertEquals(testHtmlContent, result);
        verify(templateEngine).process(eq("email/password-reset-email"), any(Context.class));
        verify(appProperties).getBaseUrl();
        verify(appProperties).getPasswordResetUrl();
    }

    @Test
    @DisplayName("Should generate suicidal alert email successfully")
    void generateSuicidalAlertEmail_ShouldGenerateEmail() {
        // Given
        String therapistName = "John Doe";
        String username = "testuser";
        String concerningMessage = "I'm feeling very depressed and don't want to live anymore.";
        LocalDateTime timestamp = LocalDateTime.now();

        // Set up AppProperties mock specifically for this test
        when(appProperties.getBaseUrl()).thenReturn(testBaseUrl);

        // When
        String result = emailTemplateService.generateSuicidalAlertEmail(therapistName, username, concerningMessage, timestamp);

        // Then
        assertEquals(testHtmlContent, result);
        verify(templateEngine).process(eq("email/suicidal-alert-email"), any(Context.class));
        verify(appProperties).getBaseUrl();
    }

    @Test
    @DisplayName("Should generate journal reminder email successfully")
    void generateJournalReminderEmail_ShouldGenerateEmail() {
        // Set up AppProperties mock specifically for this test
        when(appProperties.getBaseUrl()).thenReturn(testBaseUrl);

        // When
        String result = emailTemplateService.generateJournalReminderEmail();

        // Then
        assertEquals(testHtmlContent, result);
        verify(templateEngine).process(eq("email/journal-reminder-email"), any(Context.class));
        verify(appProperties).getBaseUrl();
    }

    @Test
    @DisplayName("Should generate milestone notification email successfully")
    void generateMilestoneNotificationEmail_ShouldGenerateEmail() {
        // Given
        String userName = "testuser";
        int streak = 7;

        // Set up AppProperties mock specifically for this test
        when(appProperties.getBaseUrl()).thenReturn(testBaseUrl);

        // When
        String result = emailTemplateService.generateMilestoneNotificationEmail(userName, streak);

        // Then
        assertEquals(testHtmlContent, result);
        verify(templateEngine).process(eq("email/milestone-notification-email"), any(Context.class));
        verify(appProperties).getBaseUrl();
    }
}
