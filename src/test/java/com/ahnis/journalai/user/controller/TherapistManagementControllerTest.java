package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistPersonalResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.service.TherapistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TherapistManagementController.class)
class TherapistManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TherapistService therapistService;

    private Therapist therapistDetails;
    private TherapistPersonalResponse therapistPersonalResponse;
    private TherapistClientResponse therapistClientResponse;
    private TherapistUpdateRequest therapistUpdateRequest;
    private Page<TherapistClientResponse> clientsPage;

    @BeforeEach
    void setUp() {
        // Setup mock therapist
        therapistDetails = new Therapist();
        therapistDetails.setId("507f1f77bcf86cd799439012");
        therapistDetails.setUsername("therapist");
        therapistDetails.setEmail("therapist@example.com");
        therapistDetails.setFirstName("John");
        therapistDetails.setLastName("Doe");
        therapistDetails.setLicenseNumber("LIC123456");
        therapistDetails.setSpecialties(Set.of("Anxiety", "Depression"));
        therapistDetails.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH));
        therapistDetails.setYearsOfExperience(5);
        therapistDetails.setBio("Professional therapist with experience in anxiety and depression treatment.");
        therapistDetails.setProfilePictureUrl("https://example.com/profile.jpg");
        therapistDetails.setClientUserId(new HashSet<>(Set.of("507f1f77bcf86cd799439011")));
        therapistDetails.setCreatedAt(Instant.now());

        // Setup therapist personal response
        therapistPersonalResponse = new TherapistPersonalResponse(
                therapistDetails.getId(),
                therapistDetails.getUsername(),
                therapistDetails.getEmail(),
                therapistDetails.getFirstName(),
                therapistDetails.getLastName(),
                therapistDetails.getLicenseNumber(),
                therapistDetails.getSpecialties(),
                therapistDetails.getLanguages(),
                therapistDetails.getYearsOfExperience(),
                therapistDetails.getBio(),
                therapistDetails.getProfilePictureUrl(),
                therapistDetails.getClientUserId().size(),
                therapistDetails.getCreatedAt()
        );

        // Setup therapist client response
        therapistClientResponse = new TherapistClientResponse(
                "507f1f77bcf86cd799439011",
                "testuser",
                "Test",
                "User",
                "test@example.com",
                Instant.now(),
                Instant.now(),
                5
        );

        // Setup therapist update request
        therapistUpdateRequest = new TherapistUpdateRequest(
                "Updated bio for testing",
                Set.of("Anxiety", "Depression", "Stress"),
                Set.of(Language.ENGLISH, Language.FRENCH, Language.GERMAN),
                7,
                "https://example.com/updated-profile.jpg"
        );

        // Setup clients page
        clientsPage = new PageImpl<>(List.of(therapistClientResponse));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"THERAPIST"})
    @DisplayName("Should get therapist profile successfully")
    void getTherapistProfile_ShouldReturnProfile() throws Exception {
        // Given
        when(therapistService.getProfile(therapistDetails.getId())).thenReturn(therapistPersonalResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(therapistDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(therapistPersonalResponse.id()))
                .andExpect(jsonPath("$.data.username").value(therapistPersonalResponse.username()))
                .andExpect(jsonPath("$.data.email").value(therapistPersonalResponse.email()));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"THERAPIST"})
    @DisplayName("Should update therapist profile successfully")
    void updateProfile_ShouldUpdateProfile() throws Exception {
        // Given
        doNothing().when(therapistService).updateProfile(eq(therapistDetails.getId()), any(TherapistUpdateRequest.class));

        // When/Then
        mockMvc.perform(patch("/api/v1/therapists/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(therapistDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(therapistUpdateRequest)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile updated"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"THERAPIST"})
    @DisplayName("Should get therapist clients successfully")
    void getMyClients_ShouldReturnClients() throws Exception {
        // Given
        when(therapistService.getClients(eq(therapistDetails.getClientUserId()), anyInt(), anyInt())).thenReturn(clientsPage);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/me/clients")
                        .with(SecurityMockMvcRequestPostProcessors.user(therapistDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(therapistClientResponse.id()))
                .andExpect(jsonPath("$.data.content[0].username").value(therapistClientResponse.username()))
                .andExpect(jsonPath("$.data.content[0].email").value(therapistClientResponse.email()));
    }
}
