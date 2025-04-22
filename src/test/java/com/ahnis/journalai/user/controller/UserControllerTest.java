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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;
    private ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse userResponse;
    private UserUpdateRequest userUpdateRequest;
    private PreferencesRequest preferencesRequest;
    private TherapistResponse therapistResponse;

    /**
     * Custom argument resolver for @AuthenticationPrincipal
     */
    static class TestUserDetailsArgumentResolver implements HandlerMethodArgumentResolver {
        private final UserDetails userDetails;

        public TestUserDetailsArgumentResolver(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                    && parameter.getParameterType().isAssignableFrom(UserDetails.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return userDetails;
        }
    }

    @BeforeEach
    void setUp() {
        // Create a test UserDetails
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        // Setup MockMvc with our custom argument resolver
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new TestUserDetailsArgumentResolver(userDetails))
                .build();

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

        therapistResponse = new TherapistResponse(
                "therapist123",
                "therapist",
                "Jane",
                "Smith",
                Set.of("Anxiety", "Depression"),
                Set.of(Language.ENGLISH),
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                "https://example.com/profile.jpg"
        );
    }

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ShouldReturnUserResponse() throws Exception {
        // Given
        when(userService.getUserResponseByUsername(anyString())).thenReturn(userResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.email").value(userResponse.email()));
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(userService).updateUserByUsername(anyString(), any(UserUpdateRequest.class));

        // When/Then
        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @DisplayName("Should update user preferences successfully")
    void updateUserPreferences_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(userService).updateUserPreferences(anyString(), any(PreferencesRequest.class));

        // When/Then
        mockMvc.perform(put("/api/v1/users/me/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferencesRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User  Preferences updated successfully"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(userService).deleteUserByUsername(anyString());

        // When/Then
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @DisplayName("Should get subscribed therapist successfully")
    void getMyTherapist_ShouldReturnTherapistResponse() throws Exception {
        // Given
        when(userService.getSubscribedTherapist(anyString())).thenReturn(therapistResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/users/me/therapist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(therapistResponse.id()))
                .andExpect(jsonPath("$.data.username").value(therapistResponse.username()))
                .andExpect(jsonPath("$.data.firstName").value(therapistResponse.firstName()))
                .andExpect(jsonPath("$.data.lastName").value(therapistResponse.lastName()));
    }
}
