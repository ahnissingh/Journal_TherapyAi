package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.exception.UserNotSubscribedException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TherapistRepository therapistRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Therapist testTherapist;
    private Preferences testPreferences;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        testPreferences = new Preferences();
        testPreferences.setReportFrequency(ReportFrequency.WEEKLY);

        testUser = new User();
        testUser.setId("507f1f77bcf86cd799439011"); // Valid ObjectId format
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPreferences(testPreferences);
        testUser.setTherapistId("507f1f77bcf86cd799439012"); // Valid ObjectId format

        testTherapist = new Therapist();
        testTherapist.setId("507f1f77bcf86cd799439012"); // Same as therapistId in testUser
        testTherapist.setUsername("therapist");
        testTherapist.setFirstName("Test");
        testTherapist.setLastName("Therapist");
        testTherapist.setSpecialties(Set.of("Anxiety", "Depression"));
        testTherapist.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH));
        testTherapist.setYearsOfExperience(5);
        testTherapist.setBio("Professional therapist with experience in anxiety and depression treatment.");
        testTherapist.setProfilePictureUrl("https://example.com/profile.jpg");

        testUserResponse = new UserResponse(
                testUser.getId(),
                testUser.getUsername(),
                testUser.getEmail(),
                null, // firstName
                null, // lastName
                Set.of(Role.USER), // roles
                null, // preferences
                Instant.now(), // nextReportOn
                null, // lastReportAt
                Instant.now(), // createdAt
                Instant.now(), // updatedAt
                0, // currentStreak
                0, // longestStreak
                null, // lastJournalEntryDate
                null  // subscribedAt
        );
    }

    @Test
    @DisplayName("Should update user report dates successfully")
    void updateUserReportDates_ShouldUpdateDates() {
        // Given
        Instant nextReportOn = Instant.now();
        Instant newNextReportOn = nextReportOn.plusSeconds(604800); // One week later

        // When
        userService.updateUserReportDates(testUser, nextReportOn, newNextReportOn);

        // Then
        verify(userRepository).updateLastReportAtById(testUser.getId(), nextReportOn);
        verify(userRepository).updateNextReportOnById(testUser.getId(), newNextReportOn);
    }

    @Test
    @DisplayName("Should get user preferences by username successfully")
    void getUserPreferencesByUsername_ShouldReturnPreferences() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // When
        Preferences result = userService.getUserPreferencesByUsername(testUser.getUsername());

        // Then
        assertNotNull(result);
        assertEquals(testPreferences, result);
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found for preferences")
    void getUserPreferencesByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UsernameNotFoundException.class, () ->
            userService.getUserPreferencesByUsername(nonExistentUsername)
        );
        verify(userRepository).findByUsername(nonExistentUsername);
    }

    @Test
    @DisplayName("Should get subscribed therapist successfully")
    void getSubscribedTherapist_ShouldReturnTherapist() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(therapistRepository.findById(testUser.getTherapistId())).thenReturn(Optional.of(testTherapist));

        // When
        TherapistResponse result = userService.getSubscribedTherapist(testUser.getUsername());

        // Then
        assertNotNull(result);
        assertEquals(testTherapist.getId(), result.id());
        assertEquals(testTherapist.getFirstName(), result.firstName());
        assertEquals(testTherapist.getLastName(), result.lastName());
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(therapistRepository).findById(testUser.getTherapistId());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found for therapist")
    void getSubscribedTherapist_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            userService.getSubscribedTherapist(nonExistentUsername)
        );
        verify(userRepository).findByUsername(nonExistentUsername);
        verify(therapistRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Should throw UserNotSubscribedException when user has no therapist")
    void getSubscribedTherapist_ShouldThrowException_WhenNoTherapist() {
        // Given
        User userWithoutTherapist = new User();
        userWithoutTherapist.setUsername(testUser.getUsername());
        userWithoutTherapist.setTherapistId(null);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(userWithoutTherapist));

        // When/Then
        assertThrows(UserNotSubscribedException.class, () ->
            userService.getSubscribedTherapist(testUser.getUsername())
        );
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(therapistRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Should get user response by username successfully")
    void getUserResponseByUsername_ShouldReturnUserResponse() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDto(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.getUserResponseByUsername(testUser.getUsername());

        // Then
        assertNotNull(result);
        assertEquals(testUserResponse, result);
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userMapper).toResponseDto(testUser);
    }

    @Test
    @DisplayName("Should update user by username successfully")
    void updateUserByUsername_ShouldUpdateUser() {
        // Given
        String newEmail = "newemail@example.com";
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        PreferencesRequest preferencesRequest = new PreferencesRequest(
            ReportFrequency.MONTHLY,
            Language.ENGLISH,
            ThemePreference.LIGHT,
            SupportStyle.FRIENDLY,
            30,
            Gender.MALE,
            true
        );

        UserUpdateRequest updateRequest = new UserUpdateRequest(
            newEmail,
            newPassword,
            preferencesRequest
        );

        Preferences newPreferences = new Preferences();
        newPreferences.setReportFrequency(ReportFrequency.MONTHLY);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userMapper.toPreferencesEntity(preferencesRequest)).thenReturn(newPreferences);

        // When
        userService.updateUserByUsername(testUser.getUsername(), updateRequest);

        // Then
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).updateEmailByUsername(testUser.getUsername(), newEmail);
        verify(userRepository).updatePasswordByUsername(testUser.getUsername(), encodedPassword);
        verify(userRepository).updatePreferencesByUsername(testUser.getUsername(), newPreferences);
        verify(userRepository).updateNextReportOnById(eq(testUser.getId()), any(Instant.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void updateUserByUsername_ShouldThrowException_WhenEmailExists() {
        // Given
        String existingEmail = "existing@example.com";
        UserUpdateRequest updateRequest = new UserUpdateRequest(
            existingEmail,
            null,
            null
        );

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        // When/Then
        assertThrows(EmailAlreadyExistsException.class, () ->
            userService.updateUserByUsername(testUser.getUsername(), updateRequest)
        );
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).existsByEmail(existingEmail);
        verify(userRepository, never()).updateEmailByUsername(anyString(), anyString());
    }

    @Test
    @DisplayName("Should update user preferences successfully")
    void updateUserPreferences_ShouldUpdatePreferences() {
        // Given
        PreferencesRequest preferencesRequest = new PreferencesRequest(
            ReportFrequency.MONTHLY,
            Language.ENGLISH,
            ThemePreference.LIGHT,
            SupportStyle.FRIENDLY,
            30,
            Gender.MALE,
            true
        );

        Preferences newPreferences = new Preferences();
        newPreferences.setReportFrequency(ReportFrequency.MONTHLY);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userMapper.toPreferencesEntity(preferencesRequest)).thenReturn(newPreferences);

        // When
        userService.updateUserPreferences(testUser.getUsername(), preferencesRequest);

        // Then
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).updatePreferencesByUsername(testUser.getUsername(), newPreferences);
        verify(userRepository).updateNextReportOnByUsername(eq(testUser.getUsername()), any(Instant.class));
    }

    @Test
    @DisplayName("Should delete user by username successfully")
    void deleteUserByUsername_ShouldDeleteUser() {
        // Given
        when(userRepository.deleteByUsername(testUser.getUsername())).thenReturn(1L);

        // When
        userService.deleteUserByUsername(testUser.getUsername());

        // Then
        verify(userRepository).deleteByUsername(testUser.getUsername());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found for deletion")
    void deleteUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUsername = "nonexistent";
        when(userRepository.deleteByUsername(nonExistentUsername)).thenReturn(0L);

        // When/Then
        assertThrows(UsernameNotFoundException.class, () ->
            userService.deleteUserByUsername(nonExistentUsername)
        );
        verify(userRepository).deleteByUsername(nonExistentUsername);
    }
}
