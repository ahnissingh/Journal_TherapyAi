package com.ahnis.journalai.analysis.scheduler;

import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
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

    //todo next asap PROFILING dev and prod
    //todo in prod and dev have cron expression in yaml
    //todo in prod have 12 am utc and in dev as required for testing set accordingly :)


    @Scheduled(cron = "0 24 19 * * ?", zone = "Asia/Kolkata")
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

        for (User user : usersDueToday) {
            try {
                Instant lastReportAt = user.getLastReportAt();
                Instant nextReportOn = user.getNextReportOn();

                Instant newNextReportOn = calculateNextReportOn(nextReportOn, user.getPreferences().getReportFrequency());
                //EDGE CASES
                //Existing user with last report generated
                if (lastReportAt != null) {
                    // For subsequent reports, using lastReportAt as the start date
                    //user reg on 23 feb  and first report on 2nd march                  (28 days in feb)
                    //eg last report was on 2nd march and today is 9th
                    //so generate report from 2nd till 9th
                    reportService.generateReport(user, lastReportAt, nextReportOn);
                } else {
                    //EDGE CASE New user using registration date as start date
                    //last report is null so user2 registers at  say 2march
                    //today is 9th march then first report so send report from 2nd till 9th
                    Instant registrationDate = user.getCreatedAt();
                    reportService.generateReport(user, registrationDate, nextReportOn);
                }
                log.info("Report generated and dates updated for user: {}", user.getUsername());
                //update last reportAt to today(nextReportOn)
                userRepository.updateLastReportAtById(user.getId(), nextReportOn);
                userRepository.updateNextReportOnById(user.getId(), newNextReportOn);
                log.info("LastReportAt and NextReportAt fields updated for user {} ", user.getUsername());

            } catch (Exception e) {
                log.error("Failed to generate report for user: {}", user.getUsername(), e);
            }
        }
    }
}
