package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistPersonalResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.exception.ConflictException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.repository.TherapistRepository;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapistServiceTest {

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TherapistService therapistServiceImpl;

    private Therapist testTherapist;
    private User testUser;
    private TherapistResponse testTherapistResponse;
    private TherapistPersonalResponse testTherapistPersonalResponse;
    private TherapistUpdateRequest testTherapistUpdateRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testTherapist = new Therapist();
        testTherapist.setId("therapist123");
        testTherapist.setUsername("therapist");
        testTherapist.setFirstName("John");
        testTherapist.setLastName("Doe");
        testTherapist.setSpecialties(Set.of("Anxiety", "Depression"));
        testTherapist.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH));
        testTherapist.setYearsOfExperience(5);
        testTherapist.setBio("Professional therapist with experience in anxiety and depression treatment.");
        testTherapist.setProfilePictureUrl("https://example.com/profile.jpg");
        testTherapist.setClientUserId(new HashSet<>());

        testUser = new User();
        testUser.setId("user123");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setTherapistId(null); // Not subscribed to any therapist initially

        testTherapistResponse = new TherapistResponse(
            testTherapist.getId(),
            testTherapist.getUsername(),
            testTherapist.getFirstName(),
            testTherapist.getLastName(),
            testTherapist.getSpecialties(),
            testTherapist.getLanguages(),
            testTherapist.getYearsOfExperience(),
            testTherapist.getBio(),
            testTherapist.getProfilePictureUrl()
        );

        testTherapistPersonalResponse = TherapistPersonalResponse.fromEntity(testTherapist);

        testTherapistUpdateRequest = new TherapistUpdateRequest(
            "Updated bio for testing",
            Set.of("Anxiety", "Depression", "Stress"),
            Set.of(Language.ENGLISH, Language.FRENCH, Language.GERMAN),
            7,
            "https://example.com/updated-profile.jpg"
        );
    }

    @Test
    @DisplayName("Should search therapists successfully")
    void search_ShouldReturnTherapists() {
        // Given
        String specialty = "Anxiety";
        String username = "therapist";
        when(mongoTemplate.find(any(Query.class), eq(Therapist.class))).thenReturn(List.of(testTherapist));

        // When
        List<TherapistResponse> result = therapistServiceImpl.search(specialty, username);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTherapistResponse.id(), result.get(0).id());
        verify(mongoTemplate).find(any(Query.class), eq(Therapist.class));
    }

    @Test
    @DisplayName("Should get all therapists successfully")
    void getAllTherapists_ShouldReturnPageOfTherapists() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Therapist> therapistPage = new PageImpl<>(List.of(testTherapist), pageable, 1);

        when(therapistRepository.findAll(any(Pageable.class))).thenReturn(therapistPage);

        // When
        Page<TherapistResponse> result = therapistServiceImpl.getAllTherapists(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testTherapistResponse.id(), result.getContent().get(0).id());
        verify(therapistRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should subscribe user to therapist successfully")
    void subscribe_ShouldSubscribeUserToTherapist() {
        // Given
        String userId = testUser.getId();
        String therapistId = testTherapist.getId();

        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(testTherapist));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        therapistServiceImpl.subscribe(userId, therapistId);

        // Then
        verify(therapistRepository).findById(therapistId);
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
        verify(therapistRepository).save(testTherapist);

        assertEquals(therapistId, testUser.getTherapistId());
        assertTrue(testTherapist.getClientUserId().contains(userId));
        assertNotNull(testUser.getSubscribedAt());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when therapist not found for subscription")
    void subscribe_ShouldThrowException_WhenTherapistNotFound() {
        // Given
        String userId = testUser.getId();
        String nonExistentTherapistId = "nonexistent";

        when(therapistRepository.findById(nonExistentTherapistId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            therapistServiceImpl.subscribe(userId, nonExistentTherapistId)
        );
        verify(therapistRepository).findById(nonExistentTherapistId);
        verify(userRepository, never()).findById(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(therapistRepository, never()).save(any(Therapist.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found for subscription")
    void subscribe_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentUserId = "nonexistent";
        String therapistId = testTherapist.getId();

        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(testTherapist));
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            therapistServiceImpl.subscribe(nonExistentUserId, therapistId)
        );
        verify(therapistRepository).findById(therapistId);
        verify(userRepository).findById(nonExistentUserId);
        verify(userRepository, never()).save(any(User.class));
        verify(therapistRepository, never()).save(any(Therapist.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when user already subscribed to a therapist")
    void subscribe_ShouldThrowException_WhenUserAlreadySubscribed() {
        // Given
        String userId = testUser.getId();
        String therapistId = testTherapist.getId();
        String existingTherapistId = "existing123";

        User subscribedUser = new User();
        subscribedUser.setId(userId);
        subscribedUser.setTherapistId(existingTherapistId);

        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(testTherapist));
        when(userRepository.findById(userId)).thenReturn(Optional.of(subscribedUser));

        // When/Then
        assertThrows(ConflictException.class, () ->
            therapistServiceImpl.subscribe(userId, therapistId)
        );
        verify(therapistRepository).findById(therapistId);
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(therapistRepository, never()).save(any(Therapist.class));
    }

    @Test
    @DisplayName("Should get therapist profile successfully")
    void getProfile_ShouldReturnTherapistProfile() {
        // Given
        String therapistId = testTherapist.getId();
        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(testTherapist));

        // When
        TherapistPersonalResponse result = therapistServiceImpl.getProfile(therapistId);

        // Then
        assertNotNull(result);
        assertEquals(testTherapist.getId(), result.id());
        verify(therapistRepository).findById(therapistId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when therapist not found for profile")
    void getProfile_ShouldThrowException_WhenTherapistNotFound() {
        // Given
        String nonExistentTherapistId = "nonexistent";
        when(therapistRepository.findById(nonExistentTherapistId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            therapistServiceImpl.getProfile(nonExistentTherapistId)
        );
        verify(therapistRepository).findById(nonExistentTherapistId);
    }

    @Test
    @DisplayName("Should update therapist profile successfully")
    void updateProfile_ShouldUpdateTherapistProfile() {
        // Given
        String therapistId = testTherapist.getId();
        when(therapistRepository.findById(therapistId)).thenReturn(Optional.of(testTherapist));

        // When
        therapistServiceImpl.updateProfile(therapistId, testTherapistUpdateRequest);

        // Then
        verify(therapistRepository).findById(therapistId);
        verify(therapistRepository).save(testTherapist);

        assertEquals(testTherapistUpdateRequest.bio(), testTherapist.getBio());
        assertEquals(testTherapistUpdateRequest.specialties(), testTherapist.getSpecialties());
        assertEquals(testTherapistUpdateRequest.spokenLanguages(), testTherapist.getLanguages());
        assertEquals(testTherapistUpdateRequest.yearsOfExperience(), testTherapist.getYearsOfExperience());
        assertEquals(testTherapistUpdateRequest.profilePictureUrl(), testTherapist.getProfilePictureUrl());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when therapist not found for profile update")
    void updateProfile_ShouldThrowException_WhenTherapistNotFound() {
        // Given
        String nonExistentTherapistId = "nonexistent";
        when(therapistRepository.findById(nonExistentTherapistId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UserNotFoundException.class, () ->
            therapistServiceImpl.updateProfile(nonExistentTherapistId, testTherapistUpdateRequest)
        );
        verify(therapistRepository).findById(nonExistentTherapistId);
        verify(therapistRepository, never()).save(any(Therapist.class));
    }

    @Test
    @DisplayName("Should get therapist clients successfully")
    void getClients_ShouldReturnPageOfClients() {
        // Given
        int page = 0;
        int size = 10;
        Set<String> clientUserIds = Set.of(testUser.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<User> clientsPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAllByIdIn(clientUserIds, pageable)).thenReturn(clientsPage);

        // When
        Page<TherapistClientResponse> result = therapistServiceImpl.getClients(clientUserIds, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser.getId(), result.getContent().get(0).id());
        verify(userRepository).findAllByIdIn(clientUserIds, pageable);
    }
}
