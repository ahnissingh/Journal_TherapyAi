package com.ahnis.journalai.analysis.service;

import com.ahnis.journalai.analysis.dto.MoodReportEmailResponse;
import com.ahnis.journalai.user.entity.Preferences;

import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.SupportStyle;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalAnalysisServiceImplTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private JournalAnalysisServiceImpl journalAnalysisService;

    private final String userId = "65c0b8dae8f5a77ac8e6e1a2";
    private final String username = "testUser";
    private Preferences userPreferences;
    private final Instant startDate = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant endDate = Instant.parse("2024-01-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        userPreferences = Preferences.builder()
                .language(Language.ENGLISH)
                .supportStyle(SupportStyle.FRIENDLY)
                .build();
    }

    @Test
    void analyzeUserMood_shouldReturnCompleteMoodReport() throws ExecutionException, InterruptedException {
        // Mock VectorStore response
        Document doc1 = new Document("Had a great day at the park with friends");
        Document doc2 = new Document("Feeling anxious about upcoming work deadline");
        List<Document> mockDocuments = List.of(doc1, doc2);

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(mockDocuments);

        // Mock ChatModel response
        String mockJsonResponse = """
                {
                    "moodSummary": "Mixed emotions with positive highlights",
                    "keyEmotions": {
                        "happiness": "45%",
                        "anxiety": "30%",
                        "excitement": "15%",
                        "frustration": "10%"
                    },
                    "insights": [
                        "Positive experiences with friends are balancing work stress",
                        "Upcoming deadlines are causing noticeable anxiety"
                    ],
                    "recommendations": [
                        "Schedule breaks between work sessions",
                        "Plan another social activity to maintain balance"
                    ],
                    "quote": "This too shall pass"
                }
                """;

        Generation generation = new Generation(new AssistantMessage(mockJsonResponse));
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse);

        // Execute the method
        CompletableFuture<MoodReportEmailResponse> future = journalAnalysisService
                .analyzeUserMood(userId, username, userPreferences, startDate, endDate);

        MoodReportEmailResponse result = future.get();

        // Verify interactions
        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
        verify(chatModel, times(1)).call(any(Prompt.class));

        // Assert results
        assertNotNull(result);
        assertEquals("Mixed emotions with positive highlights", result.moodSummary());
        assertEquals(4, result.keyEmotions().size());
        assertEquals("45%", result.keyEmotions().get("happiness"));
        assertEquals(2, result.insights().size());
        assertEquals(2, result.recommendations().size());
        assertEquals("This too shall pass", result.quote());
    }

    @Test
    void analyzeUserMood_withNoDocuments_shouldReturnEmptyReport() throws ExecutionException, InterruptedException {
        // Mock empty VectorStore response
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());

        // Mock ChatModel response for empty input
        String mockJsonResponse = """
                {
                    "moodSummary": "No journal entries found",
                    "keyEmotions": {},
                    "insights": ["No data available for analysis"],
                    "recommendations": ["Consider writing journal entries to track your mood"],
                    "quote": "Every journey begins with a single step"
                }
                """;

        Generation generation = new Generation( new AssistantMessage(mockJsonResponse));
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse);

        // Execute the method
        CompletableFuture<MoodReportEmailResponse> future = journalAnalysisService
                .analyzeUserMood(userId, username, userPreferences, startDate, endDate);

        MoodReportEmailResponse result = future.get();

        // Verify interactions
        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
        verify(chatModel, times(1)).call(any(Prompt.class));

        // Assert results
        assertNotNull(result);
        assertEquals("No journal entries found", result.moodSummary());
        assertTrue(result.keyEmotions().isEmpty());
        assertEquals(1, result.insights().size());
        assertEquals(1, result.recommendations().size());
    }

    @Test
    void generatePromptForUser_shouldIncludeAllParameters() {
        // Prepare test data
        String combinedContent = "Journal entry about feeling productive at work";
        String expectedFormat = new BeanOutputConverter<>(MoodReportEmailResponse.class).getFormat();

        // Execute the method
        String prompt = JournalAnalysisServiceImpl.generatePromptForUser(
                username, userPreferences, combinedContent);

        // Assert the prompt contains all required elements
        assertTrue(prompt.contains(MoodReportEmailResponse.class.getName()));
        assertTrue(prompt.contains(userPreferences.getLanguage().name()));
        assertTrue(prompt.contains(userPreferences.getSupportStyle().name()));
        assertTrue(prompt.contains(username));
        assertTrue(prompt.contains(combinedContent));
        assertTrue(prompt.contains(expectedFormat));

        // Verify the prompt structure matches the expected response format
        assertTrue(prompt.contains("moodSummary"));
        assertTrue(prompt.contains("keyEmotions"));
        assertTrue(prompt.contains("insights"));
        assertTrue(prompt.contains("recommendations"));
        assertTrue(prompt.contains("quote"));
    }
}
