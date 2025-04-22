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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TherapistManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TherapistService therapistService;

    @InjectMocks
    private TherapistManagementController therapistManagementController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Therapist testTherapist;
    private TherapistPersonalResponse therapistPersonalResponse;
    private TherapistUpdateRequest therapistUpdateRequest;
    private Page<TherapistClientResponse> therapistClientResponsePage;

    /**
     * Custom argument resolver for @AuthenticationPrincipal Therapist
     */
    static class TestTherapistArgumentResolver implements HandlerMethodArgumentResolver {
        private final Therapist therapist;

        public TestTherapistArgumentResolver(Therapist therapist) {
            this.therapist = therapist;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                    && parameter.getParameterType().isAssignableFrom(Therapist.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return therapist;
        }
    }

    @BeforeEach
    void setUp() {
        // Create a test Therapist
        testTherapist = Therapist.builder()
                .id("therapist123")
                .username("therapist")
                .email("therapist@example.com")
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("LIC123456")
                .specialties(Set.of("Anxiety", "Depression"))
                .languages(Set.of(Language.ENGLISH, Language.FRENCH))
                .yearsOfExperience(5)
                .bio("Professional therapist with experience in anxiety and depression treatment.")
                .profilePictureUrl("https://example.com/profile.jpg")
                .clientUserId(new HashSet<>(Set.of("user123", "user456")))
                .createdAt(Instant.now())
                .build();

        // Setup MockMvc with our custom argument resolver
        mockMvc = MockMvcBuilders.standaloneSetup(therapistManagementController)
                .setCustomArgumentResolvers(new TestTherapistArgumentResolver(testTherapist))
                .build();

        // Create test data
        therapistPersonalResponse = TherapistPersonalResponse.fromEntity(testTherapist);

        therapistUpdateRequest = new TherapistUpdateRequest(
                "Updated bio for testing",
                Set.of("Anxiety", "Depression", "Stress"),
                Set.of(Language.ENGLISH, Language.FRENCH, Language.GERMAN),
                7,
                "https://example.com/updated-profile.jpg"
        );

        // Create test client responses
        TherapistClientResponse client1 = new TherapistClientResponse(
                "user123",
                "user1",
                "John",
                "Smith",
                "user1@example.com",
                Instant.now().minus(Duration.ofDays(30)),
                Instant.now().minus(Duration.ofDays(1)),
                5
        );

        TherapistClientResponse client2 = new TherapistClientResponse(
                "user456",
                "user2",
                "Jane",
                "Doe",
                "user2@example.com",
                Instant.now().minus(Duration.ofDays(15)),
                Instant.now().minus(Duration.ofDays(2)),
                3
        );

        therapistClientResponsePage = new PageImpl<>(
                List.of(client1, client2),
                PageRequest.of(0, 10),
                2
        );
    }

    @Test
    @DisplayName("Should get therapist profile successfully")
    void getTherapistProfile_ShouldReturnTherapistPersonalResponse() throws Exception {
        // Given
        when(therapistService.getProfile(anyString())).thenReturn(therapistPersonalResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(therapistPersonalResponse.id()))
                .andExpect(jsonPath("$.data.username").value(therapistPersonalResponse.username()))
                .andExpect(jsonPath("$.data.email").value(therapistPersonalResponse.email()))
                .andExpect(jsonPath("$.data.firstname").value(therapistPersonalResponse.firstname()))
                .andExpect(jsonPath("$.data.lastName").value(therapistPersonalResponse.lastName()));
    }

    @Test
    @DisplayName("Should update therapist profile successfully")
    void updateProfile_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(therapistService).updateProfile(anyString(), any(TherapistUpdateRequest.class));

        // When/Then
        mockMvc.perform(patch("/api/v1/therapists/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(therapistUpdateRequest)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value("Profile updated"));
    }

    @Test
    @DisplayName("Should get therapist clients successfully")
    void getMyClients_ShouldReturnPageOfClients() throws Exception {
        // Given
        when(therapistService.getClients(anySet(), anyInt(), anyInt())).thenReturn(therapistClientResponsePage);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/me/clients")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value("user123"))
                .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.data.content[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$.data.content[1].id").value("user456"))
                .andExpect(jsonPath("$.data.content[1].username").value("user2"))
                .andExpect(jsonPath("$.data.content[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.data.content[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.data.content[1].email").value("user2@example.com"));
    }
}
