package com.ahnis.journalai.user.service;

import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.entity.PasswordResetToken;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.repository.PasswordResetTokenRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    private User testUser;
    private PasswordResetToken testToken;
    private final String validObjectId = "507f1f77bcf86cd799439011";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(validObjectId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        testToken = PasswordResetToken.builder()
                .id("tokenId")
                .token("test-token")
                .userId(validObjectId)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void sendPasswordResetEmail_ShouldSendEmail() {
        // Given
        when(userRepository.findByUsernameOrEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(testToken);

        // When
        passwordResetService.sendPasswordResetEmail(testUser.getEmail());

        // Then
        verify(userRepository).findByUsernameOrEmail(testUser.getEmail());
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        verify(notificationService).sendEmailPasswordReset(eq(testUser.getEmail()), anyString());

        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertEquals(testUser.getId(), capturedToken.getUserId());
        assertNotNull(capturedToken.getToken());
        assertNotNull(capturedToken.getExpiryDate());
        assertTrue(capturedToken.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found for password reset")
    void sendPasswordResetEmail_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByUsernameOrEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () ->
            passwordResetService.sendPasswordResetEmail(nonExistentEmail)
        );
        verify(userRepository).findByUsernameOrEmail(nonExistentEmail);
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(notificationService, never()).sendEmailPasswordReset(anyString(), anyString());
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ShouldResetPassword() {
        // Given
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(passwordResetTokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        when(userRepository.existsById(testToken.getUserId())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.updatePassword(testToken.getUserId(), encodedPassword)).thenReturn(1L);

        // When
        passwordResetService.resetPassword(testToken.getToken(), newPassword);

        // Then
        verify(passwordResetTokenRepository).findByToken(testToken.getToken());
        verify(userRepository).existsById(testToken.getUserId());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).updatePassword(testToken.getUserId(), encodedPassword);
        verify(passwordResetTokenRepository).delete(testToken);
    }

    @Test
    @DisplayName("Should throw RuntimeException when token not found")
    void resetPassword_ShouldThrowException_WhenTokenNotFound() {
        // Given
        String nonExistentToken = "nonexistent-token";
        String newPassword = "newPassword";

        when(passwordResetTokenRepository.findByToken(nonExistentToken)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () ->
            passwordResetService.resetPassword(nonExistentToken, newPassword)
        );
        verify(passwordResetTokenRepository).findByToken(nonExistentToken);
        verify(userRepository, never()).existsById(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when token is expired")
    void resetPassword_ShouldThrowException_WhenTokenExpired() {
        // Given
        String newPassword = "newPassword";
        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .id("tokenId")
                .token("expired-token")
                .userId(validObjectId)
                .expiryDate(Instant.now().minus(1, ChronoUnit.HOURS))
                .build();

        when(passwordResetTokenRepository.findByToken(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));

        // When/Then
        assertThrows(RuntimeException.class, () ->
            passwordResetService.resetPassword(expiredToken.getToken(), newPassword)
        );
        verify(passwordResetTokenRepository).findByToken(expiredToken.getToken());
        verify(userRepository, never()).existsById(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found for password reset")
    void resetPassword_ShouldThrowException_WhenUserNotFound() {
        // Given
        String newPassword = "newPassword";

        when(passwordResetTokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        when(userRepository.existsById(testToken.getUserId())).thenReturn(false);

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            passwordResetService.resetPassword(testToken.getToken(), newPassword)
        );
        verify(passwordResetTokenRepository).findByToken(testToken.getToken());
        verify(userRepository).existsById(testToken.getUserId());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).updatePassword(anyString(), anyString());
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when password update fails")
    void resetPassword_ShouldThrowException_WhenPasswordUpdateFails() {
        // Given
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(passwordResetTokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));
        when(userRepository.existsById(testToken.getUserId())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.updatePassword(testToken.getUserId(), encodedPassword)).thenReturn(0L);

        // When/Then
        assertThrows(RuntimeException.class, () ->
            passwordResetService.resetPassword(testToken.getToken(), newPassword)
        );
        verify(passwordResetTokenRepository).findByToken(testToken.getToken());
        verify(userRepository).existsById(testToken.getUserId());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).updatePassword(testToken.getUserId(), encodedPassword);
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
    }
}
