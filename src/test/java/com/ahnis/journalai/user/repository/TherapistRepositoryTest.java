package com.ahnis.journalai.user.repository;

import com.ahnis.journalai.config.MongoTestConfig;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.enums.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class TherapistRepositoryTest {

    @Autowired
    private TherapistRepository therapistRepository;

    private Therapist testTherapist;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        therapistRepository.deleteAll();

        // Create test therapist
        testTherapist = new Therapist();
        testTherapist.setUsername("therapist");
        testTherapist.setEmail("therapist@example.com");
        testTherapist.setPassword("encodedPassword");
        testTherapist.setLicenseNumber("LIC123456");
        testTherapist.setFirstName("John");
        testTherapist.setLastName("Doe");
        testTherapist.setYearsOfExperience(5);
        testTherapist.setBio("Professional therapist with experience in anxiety and depression treatment.");
        testTherapist.setSpecialties(Set.of("Anxiety", "Depression"));
        testTherapist.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH));
        testTherapist.setProfilePictureUrl("https://example.com/profile.jpg");
        testTherapist.setClientUserId(new HashSet<>());
        testTherapist.setCreatedAt(Instant.now());
        testTherapist.setUpdatedAt(Instant.now());
        testTherapist.setEnabled(true);
        testTherapist.setAccountNonLocked(true);
        testTherapist.setAccountNonExpired(true);
        testTherapist.setCredentialsNonExpired(true);

        // Save the test therapist
        testTherapist = therapistRepository.save(testTherapist);
    }

    @AfterEach
    void tearDown() {
        therapistRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find therapist by username")
    void findByUsernameOrEmail_ShouldReturnTherapist_WhenSearchingByUsername() {
        // When
        Optional<Therapist> result = therapistRepository.findByUsernameOrEmail(testTherapist.getUsername());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTherapist.getId(), result.get().getId());
        assertEquals(testTherapist.getUsername(), result.get().getUsername());
    }

    @Test
    @DisplayName("Should find therapist by email")
    void findByUsernameOrEmail_ShouldReturnTherapist_WhenSearchingByEmail() {
        // When
        Optional<Therapist> result = therapistRepository.findByUsernameOrEmail(testTherapist.getEmail());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTherapist.getId(), result.get().getId());
        assertEquals(testTherapist.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when therapist not found by username or email")
    void findByUsernameOrEmail_ShouldReturnEmpty_WhenTherapistNotFound() {
        // When
        Optional<Therapist> result = therapistRepository.findByUsernameOrEmail("nonexistent");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should check if therapist exists by username")
    void existsByUsernameOrEmail_ShouldReturnTrue_WhenTherapistExistsByUsername() {
        // When
        boolean result = therapistRepository.existsByUsernameOrEmail(testTherapist.getUsername(), "anyemail");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should check if therapist exists by email")
    void existsByUsernameOrEmail_ShouldReturnTrue_WhenTherapistExistsByEmail() {
        // When
        boolean result = therapistRepository.existsByUsernameOrEmail("anyusername", testTherapist.getEmail());

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when therapist does not exist by username or email")
    void existsByUsernameOrEmail_ShouldReturnFalse_WhenTherapistNotExists() {
        // When
        boolean result = therapistRepository.existsByUsernameOrEmail("nonexistent", "nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should save therapist with client user IDs")
    void save_ShouldSaveTherapistWithClientUserIds() {
        // Given
        Set<String> clientUserIds = new HashSet<>();
        clientUserIds.add("user1");
        clientUserIds.add("user2");
        testTherapist.setClientUserId(clientUserIds);

        // When
        Therapist savedTherapist = therapistRepository.save(testTherapist);

        // Then
        assertNotNull(savedTherapist);
        assertEquals(2, savedTherapist.getClientUserId().size());
        assertTrue(savedTherapist.getClientUserId().contains("user1"));
        assertTrue(savedTherapist.getClientUserId().contains("user2"));
    }

    @Test
    @DisplayName("Should update therapist fields")
    void save_ShouldUpdateTherapistFields() {
        // Given
        testTherapist.setFirstName("Updated");
        testTherapist.setLastName("Name");
        testTherapist.setBio("Updated bio");
        testTherapist.setYearsOfExperience(10);
        testTherapist.setSpecialties(Set.of("Anxiety", "Depression", "Stress"));
        testTherapist.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH, Language.GERMAN));
        testTherapist.setProfilePictureUrl("https://example.com/updated-profile.jpg");

        // When
        Therapist updatedTherapist = therapistRepository.save(testTherapist);

        // Then
        assertNotNull(updatedTherapist);
        assertEquals("Updated", updatedTherapist.getFirstName());
        assertEquals("Name", updatedTherapist.getLastName());
        assertEquals("Updated bio", updatedTherapist.getBio());
        assertEquals(10, updatedTherapist.getYearsOfExperience());
        assertEquals(3, updatedTherapist.getSpecialties().size());
        assertTrue(updatedTherapist.getSpecialties().contains("Stress"));
        assertEquals(3, updatedTherapist.getLanguages().size());
        assertTrue(updatedTherapist.getLanguages().contains(Language.GERMAN));
        assertEquals("https://example.com/updated-profile.jpg", updatedTherapist.getProfilePictureUrl());
    }

    @Test
    @DisplayName("Should delete therapist")
    void delete_ShouldDeleteTherapist() {
        // When
        therapistRepository.delete(testTherapist);

        // Then
        Optional<Therapist> deletedTherapist = therapistRepository.findById(testTherapist.getId());
        assertTrue(deletedTherapist.isEmpty());
    }
}
