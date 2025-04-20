package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User testUser;
    private UserResponse testUserResponse;
    private UserUpdateRequest userUpdateRequest;
    private UserRegistrationRequest userRegistrationRequest;
    private Preferences testPreferences;

    @BeforeEach
    void setUp() {
        // Setup test data
        testPreferences = new Preferences();
        testPreferences.setReportFrequency(ReportFrequency.WEEKLY);

        testUser = new User();
        testUser.setId("user123");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPreferences(testPreferences);
        testUser.setRoles(Set.of(Role.USER));
        testUser.setEnabled(true);
        testUser.setAccountNonLocked(true);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());

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

        PreferencesRequest preferencesRequest = new PreferencesRequest(
            ReportFrequency.MONTHLY,
            Language.ENGLISH,
            ThemePreference.LIGHT,
            SupportStyle.FRIENDLY,
            30,
            Gender.MALE,
            true
        );

        userUpdateRequest = new UserUpdateRequest(
            "newemail@example.com",
            "newPassword",
            preferencesRequest
        );

        userRegistrationRequest = new UserRegistrationRequest(
            "Test",
            "User",
            "newuser",
            "newuser@example.com",
            "password123",
            preferencesRequest,
            "America/New_York"
        );
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = List.of(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser)).thenReturn(testUserResponse);

        // When
        Page<UserResponse> result = adminService.getAllUsers(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUserResponse, result.getContent().get(0));
        verify(userRepository).findAll(any(Pageable.class));
        verify(userMapper).toResponseDto(testUser);
    }

    @Test
    @DisplayName("Should enable user successfully")
    void enableUser_ShouldEnableUser() {
        // Given
        String userId = testUser.getId();
        when(userRepository.updateEnabledStatus(userId, true)).thenReturn(1L);

        // When
        adminService.enableUser(userId);

        // Then
        verify(userRepository).updateEnabledStatus(userId, true);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when enabling non-existent user")
    void enableUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.updateEnabledStatus(nonExistentUserId, true)).thenReturn(0L);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.enableUser(nonExistentUserId)
        );
        verify(userRepository).updateEnabledStatus(nonExistentUserId, true);
    }

    @Test
    @DisplayName("Should disable user successfully")
    void disableUser_ShouldDisableUser() {
        // Given
        String userId = testUser.getId();
        when(userRepository.updateEnabledStatus(userId, false)).thenReturn(1L);

        // When
        adminService.disableUser(userId);

        // Then
        verify(userRepository).updateEnabledStatus(userId, false);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when disabling non-existent user")
    void disableUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.updateEnabledStatus(nonExistentUserId, false)).thenReturn(0L);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.disableUser(nonExistentUserId)
        );
        verify(userRepository).updateEnabledStatus(nonExistentUserId, false);
    }

    @Test
    @DisplayName("Should lock user successfully")
    void lockUser_ShouldLockUser() {
        // Given
        String userId = testUser.getId();
        when(userRepository.updateAccountNonLockedStatus(userId, false)).thenReturn(1L);

        // When
        adminService.lockUser(userId);

        // Then
        verify(userRepository).updateAccountNonLockedStatus(userId, false);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when locking non-existent user")
    void lockUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.updateAccountNonLockedStatus(nonExistentUserId, false)).thenReturn(0L);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.lockUser(nonExistentUserId)
        );
        verify(userRepository).updateAccountNonLockedStatus(nonExistentUserId, false);
    }

    @Test
    @DisplayName("Should unlock user successfully")
    void unlockUser_ShouldUnlockUser() {
        // Given
        String userId = testUser.getId();
        when(userRepository.updateAccountNonLockedStatus(userId, true)).thenReturn(1L);

        // When
        adminService.unlockUser(userId);

        // Then
        verify(userRepository).updateAccountNonLockedStatus(userId, true);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when unlocking non-existent user")
    void unlockUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.updateAccountNonLockedStatus(nonExistentUserId, true)).thenReturn(0L);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.unlockUser(nonExistentUserId)
        );
        verify(userRepository).updateAccountNonLockedStatus(nonExistentUserId, true);
    }

    @Test
    @DisplayName("Should delete user by ID successfully")
    void deleteUserById_ShouldDeleteUser() {
        // Given
        String userId = testUser.getId();
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        adminService.deleteUserById(userId);

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void deleteUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.deleteUserById(nonExistentUserId)
        );
        verify(userRepository).existsById(nonExistentUserId);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should update user by ID successfully")
    void updateUserById_ShouldUpdateUser() {
        // Given
        String userId = testUser.getId();
        String newEmail = userUpdateRequest.email();
        String newPassword = userUpdateRequest.password();
        String encodedPassword = "encodedNewPassword";
        Preferences newPreferences = new Preferences();
        newPreferences.setReportFrequency(ReportFrequency.MONTHLY);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userMapper.toPreferencesEntity(userUpdateRequest.preferences())).thenReturn(newPreferences);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDto(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = adminService.updateUserById(userId, userUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUserResponse, result);
        verify(userRepository).findById(userId);
        verify(userRepository).updateEmail(userId, newEmail);
        verify(userRepository).updatePassword(userId, encodedPassword);
        verify(userRepository).updatePreferences(userId, newPreferences);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponseDto(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void updateUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            adminService.updateUserById(nonExistentUserId, userUpdateRequest)
        );
        verify(userRepository).findById(nonExistentUserId);
        verify(userRepository, never()).updateEmail(anyString(), anyString());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
        verify(userRepository, never()).updatePreferences(anyString(), any(Preferences.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when updating with existing email")
    void updateUserById_ShouldThrowException_WhenEmailExists() {
        // Given
        String userId = testUser.getId();
        String existingEmail = userUpdateRequest.email();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        // When/Then
        assertThrows(EmailAlreadyExistsException.class, () ->
            adminService.updateUserById(userId, userUpdateRequest)
        );
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(existingEmail);
        verify(userRepository, never()).updateEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Should register multiple users successfully")
    void registerMultipleUsers_ShouldRegisterUsers() {
        // Given
        List<UserRegistrationRequest> registrationRequests = List.of(userRegistrationRequest);
        User newUser = new User();
        newUser.setUsername(userRegistrationRequest.username());
        newUser.setEmail(userRegistrationRequest.email());
        newUser.setPreferences(testPreferences);

        when(userMapper.toEntity(userRegistrationRequest)).thenReturn(newUser);
        when(passwordEncoder.encode(userRegistrationRequest.password())).thenReturn("encodedPassword");

        // When
        adminService.registerMultipleUsers(registrationRequests);

        // Then
        verify(userMapper).toEntity(userRegistrationRequest);
        verify(passwordEncoder).encode(userRegistrationRequest.password());
        verify(userRepository).saveAll(anyList());
    }
}
