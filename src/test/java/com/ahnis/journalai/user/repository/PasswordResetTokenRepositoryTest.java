package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.config.MongoTestConfig;
import com.ahnis.journalai.user.entity.PasswordResetToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private PasswordResetToken testToken;
    private final String userId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        passwordResetTokenRepository.deleteAll();

        // Create test token
        testToken = PasswordResetToken.builder()
                .token("test-token-123")
                .userId(userId)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        // Save the test token
        testToken = passwordResetTokenRepository.save(testToken);
    }

    @AfterEach
    void tearDown() {
        passwordResetTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find token by token string")
    void findByToken_ShouldReturnToken() {
        // When
        Optional<PasswordResetToken> result = passwordResetTokenRepository.findByToken(testToken.getToken());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testToken.getId(), result.get().getId());
        assertEquals(testToken.getToken(), result.get().getToken());
        assertEquals(testToken.getUserId(), result.get().getUserId());
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void findByToken_ShouldReturnEmpty_WhenTokenNotFound() {
        // When
        Optional<PasswordResetToken> result = passwordResetTokenRepository.findByToken("nonexistent-token");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete tokens by user ID")
    void deleteByUserId_ShouldDeleteTokens() {
        // Given
        PasswordResetToken anotherToken = PasswordResetToken.builder()
                .token("another-token-456")
                .userId(userId)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        passwordResetTokenRepository.save(anotherToken);

        PasswordResetToken differentUserToken = PasswordResetToken.builder()
                .token("different-user-token")
                .userId("507f1f77bcf86cd799439012") // Another valid ObjectId
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        passwordResetTokenRepository.save(differentUserToken);

        // When
        passwordResetTokenRepository.deleteByUserId(userId);

        // Then
        assertTrue(passwordResetTokenRepository.findByToken(testToken.getToken()).isEmpty());
        assertTrue(passwordResetTokenRepository.findByToken(anotherToken.getToken()).isEmpty());
        assertTrue(passwordResetTokenRepository.findByToken(differentUserToken.getToken()).isPresent());
    }

    @Test
    @DisplayName("Should save token with expiry date")
    void save_ShouldSaveTokenWithExpiryDate() {
        // Given
        Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);
        PasswordResetToken newToken = PasswordResetToken.builder()
                .token("new-token")
                .userId("507f1f77bcf86cd799439013") // Another valid ObjectId
                .expiryDate(expiryDate)
                .build();

        // When
        PasswordResetToken savedToken = passwordResetTokenRepository.save(newToken);

        // Then
        assertNotNull(savedToken.getId());
        assertEquals("new-token", savedToken.getToken());
        assertEquals("507f1f77bcf86cd799439013", savedToken.getUserId()); // Valid ObjectId format
        assertEquals(expiryDate.truncatedTo(ChronoUnit.MILLIS),
                     savedToken.getExpiryDate().truncatedTo(ChronoUnit.MILLIS));
    }
}
