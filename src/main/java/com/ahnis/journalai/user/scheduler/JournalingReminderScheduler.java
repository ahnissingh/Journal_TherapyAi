package com.ahnis.journalai.user.scheduler;

import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JournalingReminderScheduler {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 27 23 * * ?", zone = "Asia/Kolkata")
    public void remindUsersToJournal() {
        log.info("Running journaling reminder scheduler...");
        userRepository.findByRemindersEnabled(true)
                .stream()
                .filter(this::hasUserNotWrittenJournalToday)
                .forEach(user -> {
                    notificationService.sendEmailJournalReminder(user.getEmail());
                    log.info("Journaling email reminder is being sent to {}.", user.getEmail());
                });
    }

    /**
     * Check if the user has NOT written a journal entry today.
     *
     * @param user The user to check.
     * @return True if the user has NOT written a journal entry today, false otherwise.
     */
    private boolean hasUserNotWrittenJournalToday(User user) {
        ZoneId userTimeZone = ZoneId.of(user.getTimezone());
        LocalDate todayUserLocal = LocalDate.now(userTimeZone);

        if (user.getLastJournalEntryDate() == null) {
            // User ne kabhi journal nahi likha
            return true;
        }

        // Last journal ka date user ke local timezone mein convert karo
        LocalDate lastJournalDateUserLocal = user.getLastJournalEntryDate()
                .atZone(userTimeZone)
                .toLocalDate();

        // Check karo ki last journal aaj ka hai ya nahi (user ke local timezone mein)
        return !lastJournalDateUserLocal.isEqual(todayUserLocal);
    }


}
