package com.ahnis.journalai.notification.template;

import com.ahnis.journalai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    private final SpringTemplateEngine templateEngine;
    private final AppProperties appProperties;

    public String generateMoodReportEmail(MoodReportResponse report) {
        var context = new Context();
        context.setVariable("report", report);
        return templateEngine.process("mood-report-email", context);
    }

    public String generatePasswordResetEmail(String token) {
        var context = new Context();
        var resultUrl = appProperties.getBaseUrl() +
                appProperties.getPasswordResetUrl() +
                "?token=" +
                token;
        log.info("token for password reset email in Email Template service {}", token);

        context.setVariable("resetUrl", resultUrl);
        return templateEngine.process("password-reset-email", context);

    }

}
