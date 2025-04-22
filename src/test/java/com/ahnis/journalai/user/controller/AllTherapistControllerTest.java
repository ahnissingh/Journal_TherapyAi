package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.Role;
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
class AllTherapistControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TherapistService therapistService;

    @InjectMocks
    private AllTherapistController allTherapistController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private TherapistResponse therapistResponse1;
    private TherapistResponse therapistResponse2;
    private Page<TherapistResponse> therapistResponsePage;
    private List<TherapistResponse> therapistResponseList;

    /**
     * Custom argument resolver for @AuthenticationPrincipal User
     */
    static class TestUserArgumentResolver implements HandlerMethodArgumentResolver {
        private final User user;

        public TestUserArgumentResolver(User user) {
            this.user = user;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                    && parameter.getParameterType().isAssignableFrom(User.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return user;
        }
    }

    @BeforeEach
    void setUp() {
        // Create a test User
        testUser = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.USER))
                .build();

        // Setup MockMvc with our custom argument resolver
        mockMvc = MockMvcBuilders.standaloneSetup(allTherapistController)
                .setCustomArgumentResolvers(new TestUserArgumentResolver(testUser))
                .build();

        // Create test data
        therapistResponse1 = new TherapistResponse(
                "therapist123",
                "therapist1",
                "John",
                "Smith",
                Set.of("Anxiety", "Depression"),
                Set.of(Language.ENGLISH, Language.FRENCH),
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                "https://example.com/profile1.jpg"
        );

        therapistResponse2 = new TherapistResponse(
                "therapist456",
                "therapist2",
                "Jane",
                "Doe",
                Set.of("Stress", "Trauma"),
                Set.of(Language.ENGLISH, Language.GERMAN),
                8,
                "Experienced therapist specializing in stress and trauma management.",
                "https://example.com/profile2.jpg"
        );

        therapistResponsePage = new PageImpl<>(
                List.of(therapistResponse1, therapistResponse2),
                PageRequest.of(0, 10),
                2
        );

        therapistResponseList = List.of(therapistResponse1, therapistResponse2);
    }

    @Test
    @DisplayName("Should get all therapists successfully")
    void getAllTherapists_ShouldReturnPageOfTherapists() throws Exception {
        // Given
        when(therapistService.getAllTherapists(anyInt(), anyInt())).thenReturn(therapistResponsePage);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(therapistResponse1.id()))
                .andExpect(jsonPath("$.data.content[0].username").value(therapistResponse1.username()))
                .andExpect(jsonPath("$.data.content[0].firstName").value(therapistResponse1.firstName()))
                .andExpect(jsonPath("$.data.content[0].lastName").value(therapistResponse1.lastName()))
                .andExpect(jsonPath("$.data.content[1].id").value(therapistResponse2.id()))
                .andExpect(jsonPath("$.data.content[1].username").value(therapistResponse2.username()))
                .andExpect(jsonPath("$.data.content[1].firstName").value(therapistResponse2.firstName()))
                .andExpect(jsonPath("$.data.content[1].lastName").value(therapistResponse2.lastName()));
    }

    @Test
    @DisplayName("Should search therapists successfully")
    void searchTherapists_ShouldReturnListOfTherapists() throws Exception {
        // Given
        when(therapistService.search(anyString(), anyString())).thenReturn(therapistResponseList);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/search")
                .param("specialty", "Anxiety")
                .param("username", "therapist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(therapistResponse1.id()))
                .andExpect(jsonPath("$.data[0].username").value(therapistResponse1.username()))
                .andExpect(jsonPath("$.data[0].firstName").value(therapistResponse1.firstName()))
                .andExpect(jsonPath("$.data[0].lastName").value(therapistResponse1.lastName()))
                .andExpect(jsonPath("$.data[1].id").value(therapistResponse2.id()))
                .andExpect(jsonPath("$.data[1].username").value(therapistResponse2.username()))
                .andExpect(jsonPath("$.data[1].firstName").value(therapistResponse2.firstName()))
                .andExpect(jsonPath("$.data[1].lastName").value(therapistResponse2.lastName()));
    }

    @Test
    @DisplayName("Should subscribe to therapist successfully")
    void subscribe_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String therapistId = "therapist123";
        doNothing().when(therapistService).subscribe(anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/therapists/{therapistId}/subscribe", therapistId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Subscription successful"));
    }
}
