package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.config.MongoTestConfig;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Preferences testPreferences;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        userRepository.deleteAll();

        // Create test preferences
        testPreferences = new Preferences();
        testPreferences.setReportFrequency(ReportFrequency.WEEKLY);
        testPreferences.setLanguage(Language.ENGLISH);
        testPreferences.setThemePreference(ThemePreference.LIGHT);
        testPreferences.setSupportStyle(SupportStyle.FRIENDLY);
        testPreferences.setAge(30);
        testPreferences.setGender(Gender.MALE);
        testPreferences.setRemindersEnabled(true);

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setPreferences(testPreferences);
        testUser.setNextReportOn(Instant.now().plus(7, ChronoUnit.DAYS));
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());
        testUser.setEnabled(true);
        testUser.setAccountNonLocked(true);
        testUser.setAccountNonExpired(true);
        testUser.setCredentialsNonExpired(true);

        // Save the test user
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find user by username or email")
    void findByUsernameOrEmail_ShouldReturnUser() {
        // When
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail(testUser.getUsername());
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail(testUser.getEmail());

        // Then
        assertTrue(foundByUsername.isPresent());
        assertEquals(testUser.getId(), foundByUsername.get().getId());

        assertTrue(foundByEmail.isPresent());
        assertEquals(testUser.getId(), foundByEmail.get().getId());
    }

    @Test
    @DisplayName("Should return empty when user not found by username or email")
    void findByUsernameOrEmail_ShouldReturnEmpty_WhenUserNotFound() {
        // When
        Optional<User> result = userRepository.findByUsernameOrEmail("nonexistent");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should check if user exists by username or email")
    void existsByUsernameOrEmail_ShouldReturnTrue_WhenUserExists() {
        // When
        boolean existsByUsername = userRepository.existsByUsernameOrEmail(testUser.getUsername(), "anyemail");
        boolean existsByEmail = userRepository.existsByUsernameOrEmail("anyusername", testUser.getEmail());

        // Then
        assertTrue(existsByUsername);
        assertTrue(existsByEmail);
    }

    @Test
    @DisplayName("Should return false when user does not exist by username or email")
    void existsByUsernameOrEmail_ShouldReturnFalse_WhenUserNotExists() {
        // When
        boolean result = userRepository.existsByUsernameOrEmail("nonexistent", "nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        // When
        boolean result = userRepository.existsByEmail(testUser.getEmail());

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void existsByEmail_ShouldReturnFalse_WhenUserNotExists() {
        // When
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByUsername(testUser.getUsername());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty when user not found by username")
    void findByUsername_ShouldReturnEmpty_WhenUserNotFound() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should update user preferences")
    void updatePreferences_ShouldUpdatePreferences() {
        // Given
        Preferences newPreferences = new Preferences();
        newPreferences.setReportFrequency(ReportFrequency.MONTHLY);
        newPreferences.setLanguage(Language.FRENCH);
        newPreferences.setThemePreference(ThemePreference.DARK);
        newPreferences.setSupportStyle(SupportStyle.ANALYTICAL);
        newPreferences.setAge(35);
        newPreferences.setGender(Gender.FEMALE);
        newPreferences.setRemindersEnabled(false);

        // When
        userRepository.updatePreferences(testUser.getId(), newPreferences);

        // Then
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(ReportFrequency.MONTHLY, updatedUser.get().getPreferences().getReportFrequency());
        assertEquals(Language.FRENCH, updatedUser.get().getPreferences().getLanguage());
        assertEquals(ThemePreference.DARK, updatedUser.get().getPreferences().getThemePreference());
        assertEquals(SupportStyle.ANALYTICAL, updatedUser.get().getPreferences().getSupportStyle());
        assertEquals(35, updatedUser.get().getPreferences().getAge());
        assertEquals(Gender.FEMALE, updatedUser.get().getPreferences().getGender());
        assertFalse(updatedUser.get().getPreferences().isRemindersEnabled());
    }

    @Test
    @DisplayName("Should update user enabled status")
    void updateEnabledStatus_ShouldUpdateStatus() {
        // When
        long count = userRepository.updateEnabledStatus(testUser.getId(), false);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertFalse(updatedUser.get().isEnabled());
    }

    @Test
    @DisplayName("Should update user account non-locked status")
    void updateAccountNonLockedStatus_ShouldUpdateStatus() {
        // When
        long count = userRepository.updateAccountNonLockedStatus(testUser.getId(), false);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertFalse(updatedUser.get().isAccountNonLocked());
    }

    @Test
    @DisplayName("Should update user email")
    void updateEmail_ShouldUpdateEmail() {
        // Given
        String newEmail = "newemail@example.com";

        // When
        long count = userRepository.updateEmail(testUser.getId(), newEmail);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newEmail, updatedUser.get().getEmail());
    }

    @Test
    @DisplayName("Should update user password")
    void updatePassword_ShouldUpdatePassword() {
        // Given
        String newPassword = "newEncodedPassword";

        // When
        long count = userRepository.updatePassword(testUser.getId(), newPassword);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newPassword, updatedUser.get().getPassword());
    }

    @Test
    @DisplayName("Should update user password by username")
    void updatePasswordByUsername_ShouldUpdatePassword() {
        // Given
        String newPassword = "newEncodedPassword";

        // When
        long count = userRepository.updatePasswordByUsername(testUser.getUsername(), newPassword);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newPassword, updatedUser.get().getPassword());
    }

    @Test
    @DisplayName("Should update user email by username")
    void updateEmailByUsername_ShouldUpdateEmail() {
        // Given
        String newEmail = "newemail@example.com";

        // When
        long count = userRepository.updateEmailByUsername(testUser.getUsername(), newEmail);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newEmail, updatedUser.get().getEmail());
    }

    @Test
    @DisplayName("Should find users by next report date")
    void findByNextReportOn_ShouldReturnUsers() {
        // Given
        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("encodedPassword");
        user1.setPreferences(testPreferences);
        user1.setNextReportOn(now);
        userRepository.save(user1);

        // When
        List<User> users = userRepository.findByNextReportOn(startOfDay, endOfDay);

        // Then
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
    }

    @Test
    @DisplayName("Should update next report date by ID")
    void updateNextReportOnById_ShouldUpdateDate() {
        // Given
        Instant newNextReportOn = Instant.now().plus(14, ChronoUnit.DAYS);

        // When
        userRepository.updateNextReportOnById(testUser.getId(), newNextReportOn);

        // Then
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newNextReportOn.truncatedTo(ChronoUnit.MILLIS),
                     updatedUser.get().getNextReportOn().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("Should update last report date by ID")
    void updateLastReportAtById_ShouldUpdateDate() {
        // Given
        Instant newLastReportAt = Instant.now();

        // When
        userRepository.updateLastReportAtById(testUser.getId(), newLastReportAt);

        // Then
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newLastReportAt.truncatedTo(ChronoUnit.MILLIS),
                     updatedUser.get().getLastReportAt().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("Should update next report date by username")
    void updateNextReportOnByUsername_ShouldUpdateDate() {
        // Given
        Instant newNextReportOn = Instant.now().plus(14, ChronoUnit.DAYS);

        // When
        long count = userRepository.updateNextReportOnByUsername(testUser.getUsername(), newNextReportOn);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(newNextReportOn.truncatedTo(ChronoUnit.MILLIS),
                     updatedUser.get().getNextReportOn().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("Should delete user by username")
    void deleteByUsername_ShouldDeleteUser() {
        // When
        long count = userRepository.deleteByUsername(testUser.getUsername());

        // Then
        assertEquals(1, count);
        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("Should update preferences by username")
    void updatePreferencesByUsername_ShouldUpdatePreferences() {
        // Given
        Preferences newPreferences = new Preferences();
        newPreferences.setReportFrequency(ReportFrequency.MONTHLY);
        newPreferences.setLanguage(Language.FRENCH);
        newPreferences.setThemePreference(ThemePreference.DARK);
        newPreferences.setSupportStyle(SupportStyle.ANALYTICAL);
        newPreferences.setAge(35);
        newPreferences.setGender(Gender.FEMALE);
        newPreferences.setRemindersEnabled(false);

        // When
        long count = userRepository.updatePreferencesByUsername(testUser.getUsername(), newPreferences);

        // Then
        assertEquals(1, count);
        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(ReportFrequency.MONTHLY, updatedUser.get().getPreferences().getReportFrequency());
        assertEquals(Language.FRENCH, updatedUser.get().getPreferences().getLanguage());
        assertEquals(ThemePreference.DARK, updatedUser.get().getPreferences().getThemePreference());
        assertEquals(SupportStyle.ANALYTICAL, updatedUser.get().getPreferences().getSupportStyle());
        assertEquals(35, updatedUser.get().getPreferences().getAge());
        assertEquals(Gender.FEMALE, updatedUser.get().getPreferences().getGender());
        assertFalse(updatedUser.get().getPreferences().isRemindersEnabled());
    }

    @Test
    @DisplayName("Should find users by reminders enabled")
    void findByRemindersEnabled_ShouldReturnUsers() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("encodedPassword");

        Preferences preferences1 = new Preferences();
        preferences1.setReportFrequency(ReportFrequency.WEEKLY);
        preferences1.setLanguage(Language.ENGLISH);
        preferences1.setThemePreference(ThemePreference.LIGHT);
        preferences1.setSupportStyle(SupportStyle.FRIENDLY);
        preferences1.setAge(30);
        preferences1.setGender(Gender.MALE);
        preferences1.setRemindersEnabled(true);

        user1.setPreferences(preferences1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("encodedPassword");

        Preferences preferences2 = new Preferences();
        preferences2.setReportFrequency(ReportFrequency.WEEKLY);
        preferences2.setLanguage(Language.ENGLISH);
        preferences2.setThemePreference(ThemePreference.LIGHT);
        preferences2.setSupportStyle(SupportStyle.FRIENDLY);
        preferences2.setAge(30);
        preferences2.setGender(Gender.MALE);
        preferences2.setRemindersEnabled(false);

        user2.setPreferences(preferences2);
        userRepository.save(user2);

        // When
        List<User> usersWithReminders = userRepository.findByRemindersEnabled(true);
        List<User> usersWithoutReminders = userRepository.findByRemindersEnabled(false);

        // Then
        assertFalse(usersWithReminders.isEmpty());
        assertTrue(usersWithReminders.stream().allMatch(u -> u.getPreferences().isRemindersEnabled()));

        assertFalse(usersWithoutReminders.isEmpty());
        assertTrue(usersWithoutReminders.stream().noneMatch(u -> u.getPreferences().isRemindersEnabled()));
    }

    @Test
    @DisplayName("Should find users by IDs with pagination")
    void findAllByIdIn_ShouldReturnPageOfUsers() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("encodedPassword");
        user1.setPreferences(testPreferences);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("encodedPassword");
        user2.setPreferences(testPreferences);
        user2 = userRepository.save(user2);

        Set<String> userIds = new HashSet<>();
        userIds.add(testUser.getId());
        userIds.add(user1.getId());
        userIds.add(user2.getId());

        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<User> userPage = userRepository.findAllByIdIn(userIds, pageable);

        // Then
        assertEquals(3, userPage.getTotalElements());
        assertEquals(2, userPage.getContent().size());
        assertEquals(2, pageable.getPageSize());
    }
}
