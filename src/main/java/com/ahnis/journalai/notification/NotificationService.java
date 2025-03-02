package com.ahnis.journalai.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final SendGridEmailService sendGridEmailService;

    public void sendReportWithNotification(String toEmail, String reportContent) {
        var subject = "Your journal ai report";
        sendGridEmailService.sendEmail(toEmail, subject, reportContent);
    }
}
