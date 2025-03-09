package com.ahnis.journalai.analysis.scheduler;
import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.journal.repository.JournalRepository;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ReportScheduler {
    private final UserRepository userRepository;
    private final ReportService reportService;
    private final UserService userService;
    private final JournalRepository journalRepository;


    @Scheduled(cron = "0 02 20 * * ?", zone = "Asia/Kolkata")
    public void checkForReports() {
        // Get the current date in UTC
        ZonedDateTime nowInUTC = ZonedDateTime.now(ZoneOffset.UTC);
        LocalDate todayInUTC = nowInUTC.toLocalDate();

        // Convert today's date to the start and end of the day in UTC
        Instant startOfDayInUTC = todayInUTC.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDayInUTC = todayInUTC.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        log.info("Checking for reports due between: {} and {} (UTC)", startOfDayInUTC, endOfDayInUTC);

        // Fetch users who have nextReportOn equal to today
        List<User> usersDueToday = userRepository.findByNextReportOn(startOfDayInUTC, endOfDayInUTC);
        log.info("Users have report today: {}", usersDueToday);

        if (usersDueToday.isEmpty()) {
            log.info("No users found with reports due today.");
            return;
        }

        // Process users using a stream
        usersDueToday.stream()
                .peek(user -> log.info("Processing user: {}", user.getUsername()))
                .forEach(this::processUserReport);
    }
    private void processUserReport(User user) {
        try {
            Instant lastReportAt = user.getLastReportAt();
            Instant nextReportOn = user.getNextReportOn();
            Instant newNextReportOn = calculateNextReportOn(nextReportOn, user.getPreferences().getReportFrequency());

            // Generate the report based on whether the user is new or existing
            this.generateReportForUser(user, lastReportAt, nextReportOn);

            // Update the database after successful report generation
            userService.updateUserReportDates(user, nextReportOn, newNextReportOn);

            log.info("Report generated and dates updated for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to generate report for user: {}", user.getUsername(), e);
        }
    }
    private void generateReportForUser(User user, Instant lastReportAt, Instant nextReportOn) {
        if (lastReportAt != null) {
            // Existing user: Generate report from lastReportAt to nextReportOn
            reportService.generateReport(user, lastReportAt, nextReportOn);
        } else {
            // New user: Generate report from registration date to nextReportOn
            Instant registrationDate = user.getCreatedAt();
            reportService.generateReport(user, registrationDate, nextReportOn);
        }
    }
}
