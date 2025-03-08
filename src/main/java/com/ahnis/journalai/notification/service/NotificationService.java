package com.ahnis.journalai.notification.service;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.notification.template.EmailTemplateService;
import com.ahnis.journalai.user.entity.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
//todo refactor and use strategy pattern this is emailNotificationService
public class NotificationService {
    private final SendGridEmailService sendGridEmailService;
    private final EmailTemplateService emailTemplateService;

    @Async
    public void sendEmailReport(String toEmail, MoodReportResponse reportContent) {
        var subject = "Your Journal Report ";
        var htmlContent = emailTemplateService.generateMoodReportEmail(reportContent);
        sendGridEmailService.sendEmail(toEmail, subject, htmlContent);
    }

    @Async
    public void sendEmailPasswordReset(String toEmail, String token) {
        var subject = "Password request for Journal AI";
        var htmlContent = emailTemplateService.generatePasswordResetEmail(token);
        sendGridEmailService.sendEmail(toEmail, subject, htmlContent);
    }
}
