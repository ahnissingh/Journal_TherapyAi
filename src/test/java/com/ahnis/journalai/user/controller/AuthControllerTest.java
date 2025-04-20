package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.service.AuthService;
import com.ahnis.journalai.user.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// TODO: Replace @MockBean with the recommended alternative when upgrading to Spring Boot 3.4.0+
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    private AuthRequest authRequest;
    private UserRegistrationRequest userRegistrationRequest;
    private TherapistRegistrationRequest therapistRegistrationRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup auth request
        authRequest = new AuthRequest("testuser", "password123");

        // Setup user registration request
        userRegistrationRequest = new UserRegistrationRequest(
                "Test",
                "User",
                "testuser",
                "test@example.com",
                "password123",
                new com.ahnis.journalai.user.dto.request.PreferencesRequest(
                        ReportFrequency.WEEKLY,
                        Language.ENGLISH,
                        ThemePreference.LIGHT,
                        SupportStyle.FRIENDLY,
                        30,
                        Gender.MALE,
                        true
                ),
                "America/New_York"
        );

        // Setup therapist registration request
        therapistRegistrationRequest = new TherapistRegistrationRequest(
                "therapist",
                "therapist@example.com",
                "John",
                "Doe",
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                Set.of(Language.ENGLISH, Language.FRENCH),
                "password123",
                "LIC123456",
                Set.of("Anxiety", "Depression"),
                "https://example.com/profile.jpg"
        );

        // Setup auth response
        authResponse = new AuthResponse("jwt.token.here");
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_ShouldLoginUser() throws Exception {
        // Given
        when(authService.loginUser(any(AuthRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value(authResponse.token()));
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_ShouldRegisterUser() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.token").value(authResponse.token()));
    }

    @Test
    @DisplayName("Should register therapist successfully")
    void registerTherapist_ShouldRegisterTherapist() throws Exception {
        // Given
        when(authService.registerTherapist(any(TherapistRegistrationRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/register/therapist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(therapistRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Therapist registered successfully"))
                .andExpect(jsonPath("$.data.token").value(authResponse.token()));
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void forgotPassword_ShouldSendEmail() throws Exception {
        // Given
        String email = "test@example.com";
        doNothing().when(passwordResetService).sendPasswordResetEmail(email);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Password reset email sent."));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ShouldResetPassword() throws Exception {
        // Given
        String token = "reset-token";
        String newPassword = "newPassword123";
        doNothing().when(passwordResetService).resetPassword(eq(token), eq(newPassword));

        // When/Then
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .param("token", token)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Password reset successfully."));
    }
}
