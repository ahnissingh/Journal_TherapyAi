package com.ahnis.journalai.journal.service;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.journal.entity.Journal;
import com.ahnis.journalai.journal.exception.JournalNotFoundException;
import com.ahnis.journalai.journal.mapper.JournalMapper;
import com.ahnis.journalai.journal.repository.JournalRepository;
import com.ahnis.journalai.journal.embedding.JournalEmbeddingService;
import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceImplTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private JournalEmbeddingService journalEmbeddingService;

    @Mock
    private JournalMapper journalMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private JournalServiceImpl journalService;

    private Journal testJournal;
    private JournalRequest journalRequest;
    private JournalResponse journalResponse;
    private User testUser;
    private final String TEST_USER_ID = "user-123";
    private final String TEST_JOURNAL_ID = "journal-123";

    @BeforeEach
    void setUp() {
        // Create test journal
        testJournal = Journal.builder()
                .id(TEST_JOURNAL_ID)
                .title("Test Journal")
                .content("This is the content of the test journal")
                .userId(TEST_USER_ID)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
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

        // Create test user
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username("testuser")
                .email("test@example.com")
                .currentStreak(1)
                .longestStreak(5)
                .lastJournalEntryDate(Instant.now().minusSeconds(86400)) // 1 day ago
                .build();
    }

    @Test
    @DisplayName("Should create journal successfully")
    void createJournal_ShouldCreateJournal() {
        // Given
        when(journalMapper.toEntity(any(JournalRequest.class), anyString())).thenReturn(testJournal);
        when(journalRepository.save(any(Journal.class))).thenReturn(testJournal);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        doNothing().when(journalEmbeddingService).saveJournalEmbeddings(any(Journal.class));

        // When
        journalService.createJournal(journalRequest, TEST_USER_ID);

        // Then
        verify(journalMapper).toEntity(journalRequest, TEST_USER_ID);
        verify(journalRepository).save(testJournal);
        verify(journalEmbeddingService).saveJournalEmbeddings(testJournal);
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should get all journals successfully")
    void getAllJournals_ShouldReturnJournals() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Journal> journalPage = new PageImpl<>(List.of(testJournal), pageable, 1);

        when(journalRepository.findByUserId(anyString(), any(Pageable.class))).thenReturn(journalPage);
        when(journalMapper.toDto(any(Journal.class))).thenReturn(journalResponse);

        // When
        Page<JournalResponse> result = journalService.getAllJournals(TEST_USER_ID, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(journalResponse, result.getContent().get(0));
        verify(journalRepository).findByUserId(TEST_USER_ID, pageable);
        verify(journalMapper).toDto(testJournal);
    }

    @Test
    @DisplayName("Should get journal by ID successfully")
    void getJournalById_ShouldReturnJournal() {
        // Given
        when(journalRepository.findById(anyString())).thenReturn(Optional.of(testJournal));
        when(journalMapper.toDto(any(Journal.class))).thenReturn(journalResponse);

        // When
        JournalResponse result = journalService.getJournalById(TEST_JOURNAL_ID, TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse, result);
        verify(journalRepository).findById(TEST_JOURNAL_ID);
        verify(journalMapper).toDto(testJournal);
    }

    @Test
    @DisplayName("Should throw exception when journal not found")
    void getJournalById_ShouldThrowException_WhenJournalNotFound() {
        // Given
        when(journalRepository.findById(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(JournalNotFoundException.class, () ->
                journalService.getJournalById(TEST_JOURNAL_ID, TEST_USER_ID)
        );
        verify(journalRepository).findById(TEST_JOURNAL_ID);
        verify(journalMapper, never()).toDto(any(Journal.class));
    }

    @Test
    @DisplayName("Should throw exception when journal belongs to different user")
    void getJournalById_ShouldThrowException_WhenJournalBelongsToDifferentUser() {
        // Given
        String differentUserId = "different-user-id";
        when(journalRepository.findById(anyString())).thenReturn(Optional.of(testJournal));

        // When/Then
        assertThrows(JournalNotFoundException.class, () ->
                journalService.getJournalById(TEST_JOURNAL_ID, differentUserId)
        );
        verify(journalRepository).findById(TEST_JOURNAL_ID);
        verify(journalMapper, never()).toDto(any(Journal.class));
    }

    @Test
    @DisplayName("Should update journal successfully")
    void updateJournal_ShouldUpdateJournal() {
        // Given
        when(journalRepository.findById(anyString())).thenReturn(Optional.of(testJournal));
        when(journalRepository.save(any(Journal.class))).thenReturn(testJournal);
        when(journalMapper.toDto(any(Journal.class))).thenReturn(journalResponse);
        doNothing().when(journalEmbeddingService).updateJournalEmbeddings(any(Journal.class));

        // When
        JournalResponse result = journalService.updateJournal(TEST_JOURNAL_ID, journalRequest, TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse, result);
        verify(journalRepository).findById(TEST_JOURNAL_ID);
        verify(journalRepository).save(testJournal);
        verify(journalEmbeddingService).updateJournalEmbeddings(testJournal);
        verify(journalMapper).toDto(testJournal);

        // Verify journal was updated with new values
        assertEquals(journalRequest.title(), testJournal.getTitle());
        assertEquals(journalRequest.content(), testJournal.getContent());
    }

    @Test
    @DisplayName("Should delete journal successfully")
    void deleteJournal_ShouldDeleteJournal() {
        // Given
        when(journalRepository.findById(anyString())).thenReturn(Optional.of(testJournal));
        doNothing().when(journalRepository).delete(any(Journal.class));
        doNothing().when(journalEmbeddingService).deleteJournalEmbeddings(anyString());

        // When
        journalService.deleteJournal(TEST_JOURNAL_ID, TEST_USER_ID);

        // Then
        verify(journalRepository).findById(TEST_JOURNAL_ID);
        verify(journalRepository).delete(testJournal);
        verify(journalEmbeddingService).deleteJournalEmbeddings(TEST_JOURNAL_ID);
    }

    @Test
    @DisplayName("Should update user streak when creating journal")
    void createJournal_ShouldUpdateUserStreak() {
        // Given
        when(journalMapper.toEntity(any(JournalRequest.class), anyString())).thenReturn(testJournal);
        when(journalRepository.save(any(Journal.class))).thenReturn(testJournal);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        doNothing().when(journalEmbeddingService).saveJournalEmbeddings(any(Journal.class));

        // When
        journalService.createJournal(journalRequest, TEST_USER_ID);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(2, savedUser.getCurrentStreak()); // Streak should be incremented
        assertNotNull(savedUser.getLastJournalEntryDate()); // Last journal date should be updated
    }

    @Test
    @DisplayName("Should reset user streak when last entry is not from yesterday")
    void createJournal_ShouldResetUserStreak_WhenLastEntryNotFromYesterday() {
        // Given
        when(journalMapper.toEntity(any(JournalRequest.class), anyString())).thenReturn(testJournal);
        when(journalRepository.save(any(Journal.class))).thenReturn(testJournal);

        // Set last journal entry date to more than 1 day ago
        testUser.setLastJournalEntryDate(Instant.now().minusSeconds(172800)); // 2 days ago
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));

        doNothing().when(journalEmbeddingService).saveJournalEmbeddings(any(Journal.class));

        // When
        journalService.createJournal(journalRequest, TEST_USER_ID);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(1, savedUser.getCurrentStreak()); // Streak should be reset to 1
        assertNotNull(savedUser.getLastJournalEntryDate()); // Last journal date should be updated
    }

    @Test
    @DisplayName("Should update longest streak when current streak exceeds it")
    void createJournal_ShouldUpdateLongestStreak_WhenCurrentStreakExceedsIt() {
        // Given
        when(journalMapper.toEntity(any(JournalRequest.class), anyString())).thenReturn(testJournal);
        when(journalRepository.save(any(Journal.class))).thenReturn(testJournal);

        // Set current streak to equal longest streak
        testUser.setCurrentStreak(5);
        testUser.setLongestStreak(5);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));

        doNothing().when(journalEmbeddingService).saveJournalEmbeddings(any(Journal.class));

        // When
        journalService.createJournal(journalRequest, TEST_USER_ID);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(6, savedUser.getCurrentStreak()); // Streak should be incremented
        assertEquals(6, savedUser.getLongestStreak()); // Longest streak should be updated
    }
}
