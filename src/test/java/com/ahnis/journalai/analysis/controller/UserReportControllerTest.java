package com.ahnis.journalai.analysis.controller;

import com.ahnis.journalai.analysis.dto.MoodReportApiResponse;
import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.user.entity.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private UserReportController userReportController;

    private MoodReportApiResponse testApiResponse;
    private final String TEST_REPORT_ID = "507f1f77bcf86cd799439012";

    /**
     * Custom argument resolver for @AuthenticationPrincipal
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
        // Create test user
        String TEST_USER_ID = "507f1f77bcf86cd799439011";
        User testUser = User.builder()
                .id(TEST_USER_ID)
                .username("testuser")
                .email("test@example.com")
                .build();

        // Setup MockMvc with our custom argument resolver
        mockMvc = MockMvcBuilders.standaloneSetup(userReportController)
                .setCustomArgumentResolvers(new TestUserArgumentResolver(testUser))
                .build();

        // Create test mood report API response
        Map<String, String> keyEmotions = new HashMap<>();
        keyEmotions.put("happiness", "70%");
        keyEmotions.put("sadness", "10%");
        keyEmotions.put("anxiety", "20%");

        testApiResponse = new MoodReportApiResponse(
                TEST_REPORT_ID,
                Instant.now(),
                "Very positive mood with minimal sadness",
                keyEmotions,
                List.of("Your mood has improved", "You're handling stress better"),
                List.of("Keep up the good work", "Try new relaxation techniques"),
                "Every day is a new beginning.",
                Instant.now()
        );
    }

    @Test
    @DisplayName("Should get all reports successfully")
    void getAllReports_ShouldReturnPageOfReports() throws Exception {
        // Given
        int page = 0;
        int size = 10;
        Page<MoodReportApiResponse> reportPage = new PageImpl<>(
                List.of(testApiResponse),
                PageRequest.of(page, size),
                1
        );
        when(reportService.getAllReportsByUserId(anyString(), anyInt(), anyInt())).thenReturn(reportPage);

        // When/Then
        mockMvc.perform(get("/api/v1/reports")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].reportId").value(testApiResponse.reportId()))
                .andExpect(jsonPath("$.data.content[0].moodSummary").value(testApiResponse.moodSummary()));
    }

    @Test
    @DisplayName("Should get report by ID successfully")
    void getReportById_ShouldReturnReport() throws Exception {
        // Given
        when(reportService.getReportById(anyString(), anyString())).thenReturn(testApiResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/reports/{reportId}", TEST_REPORT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reportId").value(testApiResponse.reportId()))
                .andExpect(jsonPath("$.data.moodSummary").value(testApiResponse.moodSummary()));
    }

    @Test
    @DisplayName("Should get latest report successfully")
    void getLatestReport_ShouldReturnLatestReport() throws Exception {
        // Given
        when(reportService.getLatestReportByUserId(anyString())).thenReturn(testApiResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/reports/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reportId").value(testApiResponse.reportId()))
                .andExpect(jsonPath("$.data.moodSummary").value(testApiResponse.moodSummary()));
    }
}
