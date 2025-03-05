package com.ahnis.journalai.ai.analysis.scheduler;

import com.ahnis.journalai.ai.analysis.service.ReportService;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static com.ahnis.journalai.user.util.UserUtils.calculateNextReportOn;


@Component
@Slf4j
@RequiredArgsConstructor
//todo note query ke time utc me convert karke query karo
//todo save ke time mongodb automatically utc me kardega
public class ReportScheduler {
    private final UserRepository userRepository;
    private final ReportService reportService;

    //todo next asap PROFILING dev and prod
    //todo in prod and dev have cron expression in yaml
    //todo in prod have 12 am utc and in dev as required for testing set accordingly :)

    @Async
    @Scheduled(cron = "0 13 22 * * ?", zone = "Asia/Kolkata")
    public void checkForReports() {
        // Get the current date in UTC
        ZonedDateTime nowInUTC = ZonedDateTime.now(ZoneOffset.UTC);
        LocalDate todayInUTC = nowInUTC.toLocalDate();

        // Convert today's date to the start of the day in UTC
        Instant startOfDayInUTC = todayInUTC.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDayInUTC = todayInUTC.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        log.info("Checking for reports due between: {} and {} (UTC)", startOfDayInUTC, endOfDayInUTC);

        // Fetch users who have nextReportAt equal to today
        List<User> usersDueToday = userRepository.findByNextReportOn(startOfDayInUTC, endOfDayInUTC);
        log.info("Users have report today: {}", usersDueToday);

        if (usersDueToday.isEmpty()) {
            log.info("No users found with reports due today.");
            return;
        }

        // Process each user
        usersDueToday.forEach(user -> {
            try {
                Instant lastReportAt = user.getLastReportAt();
                Instant nextReportOn = user.getNextReportOn();

                if (lastReportAt != null) {
                    // Fetch journals from the vector store between lastReportAt and today
                    reportService.generateReport(user, lastReportAt, nextReportOn);
                } else { //Last report is null means new user hai idhar
                    // If lastReportAt is null, update lastReportAt and nextReportAt accordingly
                    user.setLastReportAt(nextReportOn);
                    Instant newNextReportOn = calculateNextReportOn(nextReportOn, user.getPreferences().getReportFrequency());
                    user.setNextReportOn(newNextReportOn);
                    userRepository.save(user);

                    // Generate the report
                    reportService.generateReport(user, nextReportOn, newNextReportOn);
                }

                log.info("Report generated and dates updated for user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Failed to generate report for user: {}", user.getUsername(), e);
            }
        });
    }
}
