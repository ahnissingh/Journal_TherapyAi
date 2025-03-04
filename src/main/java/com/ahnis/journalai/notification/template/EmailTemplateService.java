package com.ahnis.journalai.notification.template;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    private final SpringTemplateEngine templateEngine;

    public String generateMoodReportEmail(MoodReportResponse report) {
        var context = new Context();
        context.setVariable("report", report);
        return templateEngine.process("mood-report-email", context);
    }
}
