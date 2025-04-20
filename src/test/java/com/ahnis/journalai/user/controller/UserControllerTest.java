package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// TODO: Replace @MockBean with the recommended alternative when upgrading to Spring Boot 3.4.0+
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserServiceImpl userService;

    private UserDetails userDetails;
    private UserResponse userResponse;
    private TherapistResponse therapistResponse;
    private PreferencesRequest preferencesRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        // Setup mock user
        userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Setup user response
        userResponse = new UserResponse(
                "507f1f77bcf86cd799439011",
                "testuser",
                "test@example.com",
                "Test",
                "User",
                Set.of(Role.USER),
                new PreferencesRequest(
                        ReportFrequency.WEEKLY,
                        Language.ENGLISH,
                        ThemePreference.LIGHT,
                        SupportStyle.FRIENDLY,
                        30,
                        Gender.MALE,
                        true
                ),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                5,
                10,
                Instant.now(),
                Instant.now()
        );

        // Setup therapist response
        therapistResponse = new TherapistResponse(
                "507f1f77bcf86cd799439012",
                "therapist",
                "John",
                "Doe",
                Set.of("Anxiety", "Depression"),
                Set.of(Language.ENGLISH, Language.FRENCH),
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                "https://example.com/profile.jpg"
        );

        // Setup preferences request
        preferencesRequest = new PreferencesRequest(
                ReportFrequency.MONTHLY,
                Language.FRENCH,
                ThemePreference.DARK,
                SupportStyle.ANALYTICAL,
                35,
                Gender.FEMALE,
                false
        );

        // Setup user update request
        userUpdateRequest = new UserUpdateRequest(
                "newemail@example.com",
                "newPassword",
                preferencesRequest
        );
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserResponseByUsername("testuser")).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.email").value(userResponse.email()));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should update user successfully")
    void updateUser_ShouldUpdateUser() throws Exception {
        // Given
        doNothing().when(userService).updateUserByUsername(eq("testuser"), any(UserUpdateRequest.class));

        // When/Then
        mockMvc.perform(put("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should update user preferences successfully")
    void updateUserPreferences_ShouldUpdatePreferences() throws Exception {
        // Given
        doNothing().when(userService).updateUserPreferences(eq("testuser"), any(PreferencesRequest.class));

        // When/Then
        mockMvc.perform(put("/api/v1/users/me/preferences")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preferencesRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User  Preferences updated successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteUserByUsername("testuser");

        // When/Then
        mockMvc.perform(delete("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should get subscribed therapist successfully")
    void getMyTherapist_ShouldReturnTherapist() throws Exception {
        // Given
        when(userService.getSubscribedTherapist("testuser")).thenReturn(therapistResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/me/therapist")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(therapistResponse.id()))
                .andExpect(jsonPath("$.data.firstName").value(therapistResponse.firstName()))
                .andExpect(jsonPath("$.data.lastName").value(therapistResponse.lastName()));
    }
}
