package com.ahnis.journalai.journal.embedding;

import com.ahnis.journalai.journal.entity.Journal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEmbeddingServiceTest {

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private JournalEmbeddingService journalEmbeddingService;

    private Journal testJournal;
    private Document testDocument;
    private List<Document> splitDocuments;

    @BeforeEach
    void setUp() {
        // Create test journal
        testJournal = Journal.builder()
                .id("journal-123")
                .title("Test Journal")
                .content("This is the content of the test journal")
                .userId("user-123")
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .build();

        // Create test document
        testDocument = new Document(
                testJournal.getId(),
                testJournal.getContent(),
                Map.of(
                        "title", testJournal.getTitle(),
                        "userId", testJournal.getUserId(),
                        "createdAt", testJournal.getCreatedAt(),
                        "modifiedAt", testJournal.getModifiedAt()
                )
        );

        // Create split documents
        splitDocuments = List.of(testDocument);
    }

    @Test
    @DisplayName("Should save journal embeddings successfully")
    void saveJournalEmbeddings_ShouldSaveEmbeddings() {
        // Given
        doNothing().when(vectorStore).add(anyList());

        // When
        journalEmbeddingService.saveJournalEmbeddings(testJournal);

        // Then
        verify(vectorStore).add(anyList());
    }

    @Test
    @DisplayName("Should handle exceptions when saving journal embeddings")
    void saveJournalEmbeddings_ShouldHandleExceptions() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(vectorStore).add(anyList());

        // When
        journalEmbeddingService.saveJournalEmbeddings(testJournal);

        // Then
        // The exception is caught in the service, so the test passes if no exception is thrown
        verify(vectorStore).add(anyList());
    }

    @Test
    @DisplayName("Should update journal embeddings successfully")
    void updateJournalEmbeddings_ShouldUpdateEmbeddings() {
        // Given
        doNothing().when(vectorStore).delete(anyList());
        doNothing().when(vectorStore).add(anyList());

        // When
        journalEmbeddingService.updateJournalEmbeddings(testJournal);

        // Then
        verify(vectorStore).delete(List.of(testJournal.getId()));
        verify(vectorStore).add(anyList());
    }

    @Test
    @DisplayName("Should handle exceptions when updating journal embeddings")
    void updateJournalEmbeddings_ShouldHandleExceptions() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(vectorStore).delete(anyList());
        doNothing().when(vectorStore).add(anyList());

        // When
        journalEmbeddingService.updateJournalEmbeddings(testJournal);

        // Then
        // The exception is caught in deleteJournalEmbeddings, so updateJournalEmbeddings continues
        // and calls saveJournalEmbeddings
        verify(vectorStore).delete(List.of(testJournal.getId()));
        verify(vectorStore).add(anyList());
    }

    @Test
    @DisplayName("Should delete journal embeddings successfully")
    void deleteJournalEmbeddings_ShouldDeleteEmbeddings() {
        // Given
        doNothing().when(vectorStore).delete(anyList());

        // When
        journalEmbeddingService.deleteJournalEmbeddings(testJournal.getId());

        // Then
        verify(vectorStore).delete(List.of(testJournal.getId()));
    }

    @Test
    @DisplayName("Should handle exceptions when deleting journal embeddings")
    void deleteJournalEmbeddings_ShouldHandleExceptions() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(vectorStore).delete(anyList());

        // When
        journalEmbeddingService.deleteJournalEmbeddings(testJournal.getId());

        // Then
        verify(vectorStore).delete(List.of(testJournal.getId()));
    }
}
