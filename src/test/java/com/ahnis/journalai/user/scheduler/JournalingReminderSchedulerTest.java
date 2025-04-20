package com.ahnis.journalai.user.scheduler;

import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalingReminderSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private JournalingReminderScheduler scheduler;

    private User userWithNoJournal;
    private User userWithOldJournal;
    private User userWithTodayJournal;

    @BeforeEach
    void setUp() {
        // User who has never written a journal
        userWithNoJournal = new User();
        userWithNoJournal.setEmail("neverjournal@example.com");
        userWithNoJournal.setTimezone("America/New_York");
        userWithNoJournal.setLastJournalEntryDate(null);

        // User who wrote a journal yesterday
        userWithOldJournal = new User();
        userWithOldJournal.setEmail("oldjournal@example.com");
        userWithOldJournal.setTimezone("America/New_York");
        ZoneId zoneId = ZoneId.of("America/New_York");
        LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);
        Instant yesterdayInstant = yesterday.atStartOfDay(zoneId).toInstant();
        userWithOldJournal.setLastJournalEntryDate(yesterdayInstant);

        // User who wrote a journal today
        userWithTodayJournal = new User();
        userWithTodayJournal.setEmail("todayjournal@example.com");
        userWithTodayJournal.setTimezone("America/New_York");
        LocalDate today = LocalDate.now(zoneId);
        Instant todayInstant = today.atStartOfDay(zoneId).toInstant();
        userWithTodayJournal.setLastJournalEntryDate(todayInstant);
    }

    @Test
    @DisplayName("Should send reminders to users who haven't journaled today")
    void remindUsersToJournal_ShouldSendReminders_ToUsersWhoHaventJournaledToday() {
        // Given
        when(userRepository.findByRemindersEnabled(true))
                .thenReturn(List.of(userWithNoJournal, userWithOldJournal, userWithTodayJournal));

        // When
        scheduler.remindUsersToJournal();

        // Then
        verify(notificationService).sendEmailJournalReminder(userWithNoJournal.getEmail());
        verify(notificationService).sendEmailJournalReminder(userWithOldJournal.getEmail());
        verify(notificationService, never()).sendEmailJournalReminder(userWithTodayJournal.getEmail());
    }

    @Test
    @DisplayName("Should not send reminders when no users have reminders enabled")
    void remindUsersToJournal_ShouldNotSendReminders_WhenNoUsersHaveRemindersEnabled() {
        // Given
        when(userRepository.findByRemindersEnabled(true)).thenReturn(List.of());

        // When
        scheduler.remindUsersToJournal();

        // Then
        verify(notificationService, never()).sendEmailJournalReminder(anyString());
    }

    @Test
    @DisplayName("Should handle users with different timezones correctly")
    void remindUsersToJournal_ShouldHandleUsersWithDifferentTimezones() {
        // Given
        User userInDifferentTimezone = new User();
        userInDifferentTimezone.setEmail("differenttz@example.com");
        userInDifferentTimezone.setTimezone("Asia/Tokyo"); // Different timezone

        // Set last journal entry to today in Tokyo time
        ZoneId tokyoZone = ZoneId.of("Asia/Tokyo");
        LocalDate tokyoToday = LocalDate.now(tokyoZone);
        Instant tokyoTodayInstant = tokyoToday.atStartOfDay(tokyoZone).toInstant();
        userInDifferentTimezone.setLastJournalEntryDate(tokyoTodayInstant);

        when(userRepository.findByRemindersEnabled(true))
                .thenReturn(List.of(userInDifferentTimezone));

        // When
        scheduler.remindUsersToJournal();

        // Then
        verify(notificationService, never()).sendEmailJournalReminder(userInDifferentTimezone.getEmail());
    }


}
