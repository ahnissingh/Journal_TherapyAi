package com.ahnis.journalai.journal.repository;

import com.ahnis.journalai.config.MongoTestConfig;
import com.ahnis.journalai.journal.entity.Journal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class JournalRepositoryTest {

    @Autowired
    private JournalRepository journalRepository;

    private Journal testJournal1;
    private Journal testJournal2;
    private final String TEST_USER_ID = "507f1f77bcf86cd799439011";
    private final String OTHER_USER_ID = "507f1f77bcf86cd799439012";

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        journalRepository.deleteAll();

        // Create test journals
        testJournal1 = Journal.builder()
                .title("Test Journal 1")
                .content("This is the content of test journal 1")
                .userId(TEST_USER_ID)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .build();

        testJournal2 = Journal.builder()
                .title("Test Journal 2")
                .content("This is the content of test journal 2")
                .userId(TEST_USER_ID)
                .createdAt(Instant.now().plusSeconds(3600)) // 1 hour later
                .modifiedAt(Instant.now().plusSeconds(3600))
                .build();

        // Save the test journals
        testJournal1 = journalRepository.save(testJournal1);
        testJournal2 = journalRepository.save(testJournal2);
    }

    @AfterEach
    void tearDown() {
        journalRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find journals by userId with pagination")
    void findByUserId_ShouldReturnJournals() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // When
        Page<Journal> result = journalRepository.findByUserId(TEST_USER_ID, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        // Verify sorting (newest first)
        assertEquals(testJournal2.getId(), result.getContent().get(0).getId());
        assertEquals(testJournal1.getId(), result.getContent().get(1).getId());
    }

    @Test
    @DisplayName("Should return empty page when no journals found for userId")
    void findByUserId_ShouldReturnEmptyPage_WhenNoJournalsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Journal> result = journalRepository.findByUserId(OTHER_USER_ID, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void findByUserId_ShouldHandlePaginationCorrectly() {
        // Given
        // Create additional journals to test pagination
        for (int i = 3; i <= 12; i++) {
            Journal journal = Journal.builder()
                    .title("Test Journal " + i)
                    .content("This is the content of test journal " + i)
                    .userId(TEST_USER_ID)
                    .createdAt(Instant.now().plusSeconds(i * 3600)) // Each journal is 1 hour later
                    .modifiedAt(Instant.now().plusSeconds(i * 3600))
                    .build();
            journalRepository.save(journal);
        }

        // When - Page 0, Size 5
        Pageable pageRequest1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Journal> page1 = journalRepository.findByUserId(TEST_USER_ID, pageRequest1);

        // Then
        assertEquals(12, page1.getTotalElements()); // Total of 12 journals
        assertEquals(5, page1.getContent().size()); // 5 journals per page
        assertEquals(0, page1.getNumber()); // Page number is 0
        assertEquals(3, page1.getTotalPages()); // Total of 3 pages

        // When - Page 1, Size 5
        Pageable pageRequest2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Journal> page2 = journalRepository.findByUserId(TEST_USER_ID, pageRequest2);

        // Then
        assertEquals(12, page2.getTotalElements());
        assertEquals(5, page2.getContent().size());
        assertEquals(1, page2.getNumber()); // Page number is 1

        // Verify different content on different pages
        assertNotEquals(
            page1.getContent().get(0).getId(),
            page2.getContent().get(0).getId()
        );
    }

    @Test
    @DisplayName("Should handle empty repository")
    void findByUserId_ShouldHandleEmptyRepository() {
        // Given
        journalRepository.deleteAll();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Journal> result = journalRepository.findByUserId(TEST_USER_ID, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}
