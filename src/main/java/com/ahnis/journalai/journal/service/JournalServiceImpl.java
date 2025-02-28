package com.ahnis.journalai.journal.service;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.journal.entity.Journal;
import com.ahnis.journalai.journal.exception.JournalNotFoundException;
import com.ahnis.journalai.journal.mapper.JournalMapper;
import com.ahnis.journalai.journal.repository.JournalRepository;
import com.ahnis.journalai.ai.embedding.JournalEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {
    private final JournalRepository journalRepository;
    private final JournalMapper journalMapper;
    private final JournalEmbeddingService journalEmbeddingService;

    @Override
    public JournalResponse createJournal(JournalRequest dto, String userId) {
        Journal journal = journalMapper.toEntity(dto, userId);
        Journal savedJournal = journalRepository.save(journal);
        journalEmbeddingService.saveJournalEmbeddings(savedJournal);
        return journalMapper.toDto(savedJournal);
    }

    @Override
    public List<JournalResponse> getAllJournals(String userId) {
        return journalRepository.findByUserId(userId)
                .stream()
                .map(journalMapper::toDto)
                .toList();
    }

    @Override
    public JournalResponse getJournalById(String id, String userId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, userId);
        return journalMapper.toDto(journal);
    }

    @Override
    public JournalResponse updateJournal(String id, JournalRequest dto, String userId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, userId);

        journal.setTitle(dto.title());
        journal.setContent(dto.content());
        return journalMapper.toDto(journalRepository.save(journal));
    }

    @Override
    public void deleteJournal(String id, String userId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, userId);
        journalRepository.delete(journal);
    }

    private void validateJournalOwnership(Journal journal, String userId) {
        if (!journal.getUserId().equals(userId)) {
            throw new JournalNotFoundException("Journal not found");
        }
    }
}
