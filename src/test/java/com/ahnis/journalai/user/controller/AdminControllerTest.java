package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private AuthService authService;

    private UserResponse userResponse;
    private UserUpdateRequest userUpdateRequest;
    private List<UserRegistrationRequest> userRegistrationRequests;
    private Page<UserResponse> usersPage;

    @BeforeEach
    void setUp() {
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

        // Setup user update request
        userUpdateRequest = new UserUpdateRequest(
                "newemail@example.com",
                "newPassword",
                new PreferencesRequest(
                        ReportFrequency.MONTHLY,
                        Language.FRENCH,
                        ThemePreference.DARK,
                        SupportStyle.ANALYTICAL,
                        35,
                        Gender.FEMALE,
                        false
                )
        );

        // Setup user registration requests
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "New",
                "User",
                "newuser",
                "newuser@example.com",
                "password123",
                new PreferencesRequest(
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
        userRegistrationRequests = List.of(registrationRequest);

        // Setup users page
        usersPage = new PageImpl<>(List.of(userResponse));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnUsers() throws Exception {
        // Given
        when(adminService.getAllUsers(anyInt(), anyInt())).thenReturn(usersPage);

        // When/Then
        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.content[0].username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.content[0].email").value(userResponse.email()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should create users successfully")
    void createUsers_ShouldCreateUsers() throws Exception {
        // Given
        doNothing().when(authService).registerUser(any(UserRegistrationRequest.class));

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        doNothing().when(adminService).deleteUserById(userId);

        // When/Then
        mockMvc.perform(delete("/api/v1/admin/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should update user successfully")
    void updateUser_ShouldUpdateUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        when(adminService.updateUserById(eq(userId), any(UserUpdateRequest.class))).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(put("/api/v1/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.email").value(userResponse.email()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should enable user successfully")
    void enableUser_ShouldEnableUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        doNothing().when(adminService).enableUser(userId);

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/enable", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User enabled successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should disable user successfully")
    void disableUser_ShouldDisableUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        doNothing().when(adminService).disableUser(userId);

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/disable", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User disabled successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should lock user successfully")
    void lockUser_ShouldLockUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        doNothing().when(adminService).lockUser(userId);

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/lock", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User locked successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should unlock user successfully")
    void unlockUser_ShouldUnlockUser() throws Exception {
        // Given
        String userId = "507f1f77bcf86cd799439011";
        doNothing().when(adminService).unlockUser(userId);

        // When/Then
        mockMvc.perform(post("/api/v1/admin/users/{userId}/unlock", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User unlocked successfully"));
    }
}
