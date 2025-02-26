package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.journal.JournalRequest;
import com.ahnis.journalai.dto.journal.JournalResponse;
import com.ahnis.journalai.entity.Journal;
import com.ahnis.journalai.exception.custom.JournalNotFoundException;
import com.ahnis.journalai.mapper.JournalMapper;
import com.ahnis.journalai.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {
    private final JournalRepository journalRepository;
    private final JournalMapper journalMapper;

    @Override
    public JournalResponse createJournal(JournalRequest dto, String userId) {
        Journal journal = journalMapper.toEntity(dto, userId);
        return journalMapper.toDto(journalRepository.save(journal));
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
