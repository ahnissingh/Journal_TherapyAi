package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.service.AdminService;
import com.ahnis.journalai.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse userResponse;
    private UserUpdateRequest userUpdateRequest;
    private PreferencesRequest preferencesRequest;
    private UserRegistrationRequest userRegistrationRequest;
    private Page<UserResponse> userResponsePage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .build();

        // Setup test data
        preferencesRequest = new PreferencesRequest(
                ReportFrequency.WEEKLY,
                Language.ENGLISH,
                ThemePreference.LIGHT,
                SupportStyle.FRIENDLY,
                30,
                Gender.MALE,
                true
        );

        userResponse = new UserResponse(
                "user123",
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                Set.of(Role.USER),
                preferencesRequest,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                5,
                10,
                Instant.now(),
                Instant.now()
        );

        userUpdateRequest = new UserUpdateRequest(
                "updated@example.com",
                "newPassword123",
                preferencesRequest
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

        userResponsePage = new PageImpl<>(
                List.of(userResponse),
                PageRequest.of(0, 10),
                1
        );
    }

    @Test
    @DisplayName("Should get all users successfully")
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
        // Given
        when(adminService.getAllUsers(anyInt(), anyInt())).thenReturn(userResponsePage);

        // When/Then
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.content[0].username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.content[0].email").value(userResponse.email()));
    }

    @Test
    @DisplayName("Should create users successfully")
    @WithMockUser(roles = "ADMIN")
    void createUsers_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(authService.registerUser(any(UserRegistrationRequest.class))).thenReturn(new AuthResponse("jwt-token-123"));

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(userRegistrationRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Users created successfully"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String userId = "user123";
        doNothing().when(adminService).deleteUserById(anyString());

        // When/Then
        mockMvc.perform(delete("/api/v1/admin/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @DisplayName("Should update user successfully")
    @WithMockUser(roles = "ADMIN")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        String userId = "user123";
        when(adminService.updateUserById(anyString(), any(UserUpdateRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(put("/api/v1/admin/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.email").value(userResponse.email()));
    }

    @Test
    @DisplayName("Should enable user successfully")
    @WithMockUser(roles = "ADMIN")
    void enableUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String userId = "user123";
        doNothing().when(adminService).enableUser(anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/enable", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User enabled successfully"));
    }

    @Test
    @DisplayName("Should disable user successfully")
    @WithMockUser(roles = "ADMIN")
    void disableUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String userId = "user123";
        doNothing().when(adminService).disableUser(anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/disable", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User disabled successfully"));
    }

    @Test
    @DisplayName("Should lock user successfully")
    @WithMockUser(roles = "ADMIN")
    void lockUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String userId = "user123";
        doNothing().when(adminService).lockUser(anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/lock", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User locked successfully"));
    }

    @Test
    @DisplayName("Should unlock user successfully")
    @WithMockUser(roles = "ADMIN")
    void unlockUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String userId = "user123";
        doNothing().when(adminService).unlockUser(anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/unlock", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User unlocked successfully"));
    }
}
