package com.ahnis.journalai.user.scheduler;

import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JournalingReminderScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 24 5 * * ?", zone = "Asia/Kolkata") // Subah 7 baje IST mein chale
    public void remindUsersToJournal() {
        log.info("Running journaling reminder scheduler...");

        // Sabhi users ko fetch karo jinke reminders enabled hai
        List<User> users = userRepository.findByRemindersEnabled(true);

        for (User user : users) {
            // User ka local timezone mein aaj ka date nikal lo
            ZoneId userTimeZone = ZoneId.of(user.getTimezone());
            LocalDate todayUserLocal = LocalDate.now(userTimeZone);

            // Check karo ki user ne aaj (local date) journal likha hai ya nahi
            if (hasUserWrittenJournalToday(user, todayUserLocal, userTimeZone)) {
                log.info("User {} ne aaj journal likh liya hai. Reminder ki zarurat nahi hai.", user.getEmail());
            } else {
                // Reminder bhejo
                log.info("User {} ko reminder bheja ja raha hai.", user.getEmail());
                // Yahan email ya notification service integrate kar sakte ho
            }
        }

    }

    private boolean hasUserWrittenJournalToday(User user, LocalDate todayUserLocal, ZoneId userTimeZone) {
        if (user.getLastJournalEntryDate() == null) {
            // User ne kabhi journal nahi likha
            return false;
        }

        // Last journal ka date user ke local timezone mein convert karo
        LocalDate lastJournalDateUserLocal = user.getLastJournalEntryDate()
                .atZone(userTimeZone)
                .toLocalDate();

        // Check karo ki last journal aaj ka hai ya nahi (user ke local timezone mein)
        return lastJournalDateUserLocal.isEqual(todayUserLocal);
    }
}
