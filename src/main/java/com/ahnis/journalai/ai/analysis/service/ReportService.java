package com.ahnis.journalai.ai.analysis.service;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.ai.analysis.entity.MoodReportEntity;
import com.ahnis.journalai.ai.analysis.repository.ReportRepository;
import com.ahnis.journalai.notification.NotificationService;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final JournalAnalysisService journalAnalysisService;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;

    @Async
    public void generateAndSaveReport(User user) {
        try {
            MoodReportResponse moodReport = journalAnalysisService.analyzeUserMood(user.getId()).join();

            //Entity
            MoodReportEntity reportEntity = MoodReportEntity.builder()
                    .userId(user.getId())
                    .reportDate(LocalDate.now())
                    .moodSummary(moodReport.moodSummary())
                    .keyEmotions(moodReport.keyEmotions())
                    .contextualInsights(moodReport.contextualInsights())
                    .recommendations(moodReport.recommendations())
                    .build();

            reportRepository.save(reportEntity);
            sendReport(user, moodReport);
            log.info("Report generated and saved for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to generate report for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    @Async
    protected void sendReport(User user, MoodReportResponse report) {
        // Implement email and in-app notification logic here
        log.info("Sending report to user: {}", user.getUsername());
//        notificationService.sendReportWithNotification(user.getEmail(), report.toString());
        notificationService.sendReportWithNotification("ahnisaneja@gmail.com", report.toString());
    }
}
