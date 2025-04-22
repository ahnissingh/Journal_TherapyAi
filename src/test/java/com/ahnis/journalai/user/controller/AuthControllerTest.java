package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.config.TestSecurityConfig;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.PreferencesRequest;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private AuthRequest authRequest;
    private UserRegistrationRequest userRegistrationRequest;
    private TherapistRegistrationRequest therapistRegistrationRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Setup test data
        authRequest = new AuthRequest("testuser", "password123");

        // Create PreferencesRequest
        PreferencesRequest preferencesRequest = new PreferencesRequest(
                ReportFrequency.WEEKLY,
                Language.ENGLISH,
                ThemePreference.LIGHT,
                SupportStyle.FRIENDLY,
                30,
                Gender.MALE,
                true
        );

        userRegistrationRequest = new UserRegistrationRequest(
                "John",
                "Doe",
                "testuser",
                "test@example.com",
                "password123",
                preferencesRequest,
                "America/New_York"
        );

        therapistRegistrationRequest = new TherapistRegistrationRequest(
                "therapist",
                "therapist@example.com",
                "Jane",
                "Smith",
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                Set.of(Language.ENGLISH),
                "password123",
                "LIC123456",
                Set.of("Anxiety", "Depression"),
                "https://example.com/profile.jpg"
        );

        authResponse = new AuthResponse("jwt-token-123");
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.loginUser(any(AuthRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value(authResponse.token()));
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationRequest.class))).thenReturn(authResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/register/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.token").value(authResponse.token()));
    }

    @Test
    @DisplayName("Should register therapist successfully")
    void registerTherapist_ShouldReturnAuthResponse() throws Exception {
        // Given
        AuthResponse therapistAuthResponse = new AuthResponse("jwt-token-456");
        when(authService.registerTherapist(any(TherapistRegistrationRequest.class))).thenReturn(therapistAuthResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/register/therapist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(therapistRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Therapist registered successfully"))
                .andExpect(jsonPath("$.data.token").value(therapistAuthResponse.token()));
    }

    @Test
    @DisplayName("Should request password reset successfully")
    void forgotPassword_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String email = "test@example.com";
        doNothing().when(passwordResetService).sendPasswordResetEmail(anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String token = "reset-token-123";
        String newPassword = "newPassword123";
        doNothing().when(passwordResetService).resetPassword(anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .param("token", token)
                .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}
