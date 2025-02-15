package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.JournalRequestDTO;
import com.ahnis.journalai.dto.JournalResponseDTO;
import com.ahnis.journalai.entity.Journal;
import com.ahnis.journalai.entity.User;
import com.ahnis.journalai.exception.JournalNotFoundException;
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
    public JournalResponseDTO createJournal(JournalRequestDTO dto, User user) {
        Journal journal = journalMapper.toEntity(dto, user);
        return journalMapper.toDto(journalRepository.save(journal));
    }

    @Override
    public List<JournalResponseDTO> getAllJournals(User user) {
        return journalRepository.findByUser_Id(user.getId())
                .stream()
                .map(journalMapper::toDto)
                .toList();
    }

    @Override
    public JournalResponseDTO getJournalById(String id, User user) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, user);
        return journalMapper.toDto(journal);
    }

    @Override
    public JournalResponseDTO updateJournal(String id, JournalRequestDTO dto, User user) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, user);

        journal.setTitle(dto.title());
        journal.setContent(dto.content());
        return journalMapper.toDto(journalRepository.save(journal));
    }

    @Override
    public void deleteJournal(String id, User user) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException("Journal not found"));
        validateJournalOwnership(journal, user);
        journalRepository.delete(journal);
    }

    private void validateJournalOwnership(Journal journal, User user) {
        if (!journal.getUser().getId().equals(user.getId())) {
            throw new JournalNotFoundException("Journal not found");
        }
    }
}
