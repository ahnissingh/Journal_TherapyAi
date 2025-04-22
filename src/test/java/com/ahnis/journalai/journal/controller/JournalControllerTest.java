package com.ahnis.journalai.journal.controller;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.journal.service.JournalService;
import com.ahnis.journalai.user.entity.User;
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
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JournalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JournalService journalService;

    @InjectMocks
    private JournalController journalController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private JournalRequest journalRequest;
    private JournalResponse journalResponse;
    private User testUser;
    private final String TEST_USER_ID = "user-123";
    private final String TEST_JOURNAL_ID = "journal-123";

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
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username("testuser")
                .email("test@example.com")
                .build();

        // Setup MockMvc with our custom argument resolver
        mockMvc = MockMvcBuilders.standaloneSetup(journalController)
                .setCustomArgumentResolvers(new TestUserArgumentResolver(testUser))
                .build();

        // Create test journal request
        journalRequest = new JournalRequest("Test Journal", "This is the content of the test journal");

        // Create test journal response
        journalResponse = new JournalResponse(
                TEST_JOURNAL_ID,
                "Test Journal",
                "This is the content of the test journal",
                Instant.now(),
                Instant.now(),
                TEST_USER_ID
        );
    }

    @Test
    @DisplayName("Should create journal successfully")
    void createJournal_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(journalService).createJournal(any(JournalRequest.class), anyString());

        // When/Then
        mockMvc.perform(post("/api/v1/journals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Posted Journal"));

        verify(journalService).createJournal(journalRequest, TEST_USER_ID);
    }

    @Test
    @DisplayName("Should get all journals successfully")
    void getAllJournals_ShouldReturnJournals() throws Exception {
        // Given
        Page<JournalResponse> journalPage = new PageImpl<>(
                List.of(journalResponse),
                PageRequest.of(0, 10),
                1
        );
        when(journalService.getAllJournals(anyString(), anyInt(), anyInt())).thenReturn(journalPage);

        // When/Then
        mockMvc.perform(get("/api/v1/journals")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(journalResponse.id()))
                .andExpect(jsonPath("$.data.content[0].title").value(journalResponse.title()))
                .andExpect(jsonPath("$.data.content[0].content").value(journalResponse.content()));

        verify(journalService).getAllJournals(TEST_USER_ID, 0, 10);
    }

    @Test
    @DisplayName("Should get journal by ID successfully")
    void getJournalById_ShouldReturnJournal() throws Exception {
        // Given
        when(journalService.getJournalById(anyString(), anyString())).thenReturn(journalResponse);

        // When/Then
        mockMvc.perform(get("/api/v1/journals/{id}", TEST_JOURNAL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(journalResponse.id()))
                .andExpect(jsonPath("$.data.title").value(journalResponse.title()))
                .andExpect(jsonPath("$.data.content").value(journalResponse.content()));

        verify(journalService).getJournalById(TEST_JOURNAL_ID, TEST_USER_ID);
    }

    @Test
    @DisplayName("Should update journal successfully")
    void updateJournal_ShouldReturnUpdatedJournal() throws Exception {
        // Given
        when(journalService.updateJournal(anyString(), any(JournalRequest.class), anyString())).thenReturn(journalResponse);

        // When/Then
        mockMvc.perform(put("/api/v1/journals/{id}", TEST_JOURNAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.message").value("Journal updated successfully"))
                .andExpect(jsonPath("$.data.id").value(journalResponse.id()))
                .andExpect(jsonPath("$.data.title").value(journalResponse.title()))
                .andExpect(jsonPath("$.data.content").value(journalResponse.content()));

        verify(journalService).updateJournal(TEST_JOURNAL_ID, journalRequest, TEST_USER_ID);
    }

    @Test
    @DisplayName("Should delete journal successfully")
    void deleteJournal_ShouldReturnSuccessResponse() throws Exception {
        // Given
        doNothing().when(journalService).deleteJournal(anyString(), anyString());

        // When/Then
        mockMvc.perform(delete("/api/v1/journals/{id}", TEST_JOURNAL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value("Journal deleted successfully"));

        verify(journalService).deleteJournal(TEST_JOURNAL_ID, TEST_USER_ID);
    }

    @Test
    @DisplayName("Should return bad request when journal request is invalid")
    void createJournal_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Given
        JournalRequest invalidRequest = new JournalRequest("", ""); // Empty title and content

        // When/Then
        mockMvc.perform(post("/api/v1/journals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(journalService, never()).createJournal(any(JournalRequest.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when updating with invalid journal request")
    void updateJournal_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Given
        JournalRequest invalidRequest = new JournalRequest("", ""); // Empty title and content

        // When/Then
        mockMvc.perform(put("/api/v1/journals/{id}", TEST_JOURNAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(journalService, never()).updateJournal(anyString(), any(JournalRequest.class), anyString());
    }
}
