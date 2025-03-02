package com.ahnis.journalai.ai.analysis.scheduler;

import com.ahnis.journalai.ai.analysis.service.ReportService;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;


@Component
@Slf4j
@RequiredArgsConstructor
//todo note query ke time utc me convert karke query karo
//todo save ke time mongodb automatically utc me kardega
public class ReportScheduler {
    private final UserRepository userRepository;
    private final ReportService reportService;

    @Scheduled(cron = "0 30 19 * * ?", zone = "Asia/Kolkata")
    public void checkForReports() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        var startOfDay = today.atStartOfDay(ZoneOffset.UTC).toLocalDate();
        var endOfDay = startOfDay.plusDays(1);
        log.info("Checking for reports due between: {} and {}", startOfDay, endOfDay);
        var usersDueToday = userRepository.findByNextReportOn(startOfDay, endOfDay);
        log.info("users have report today {}", Arrays.toString(usersDueToday.toArray()));

        if (usersDueToday.isEmpty()) {
            log.error("Users are empty");
            return;
        }
        for (var user : usersDueToday) {
            try {
                //Step1 : generate and save report
                reportService.generateAndSaveReport(user);
                //Step 2: update/increment nextReportOn
                LocalDate nextReportOn = UserUtils
                        .calculateNextReportOn(today, user.getPreferences().getReportFrequency());

                userRepository.updateByIdAndNextReportOn(user.getId(), nextReportOn);
                log.info("Report generated and nextReportOn updated for user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Failed to generate report for user: {}", user.getUsername(), e);
            }
        }
    }

}
