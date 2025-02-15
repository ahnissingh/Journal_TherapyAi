package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.JournalRequestDTO;
import com.ahnis.journalai.dto.JournalResponseDTO;
import com.ahnis.journalai.entity.User;

import java.util.List;

public interface JournalService {
    JournalResponseDTO createJournal(JournalRequestDTO dto, User user);
    List<JournalResponseDTO> getAllJournals(User user);
    JournalResponseDTO getJournalById(String id, User user);
    JournalResponseDTO updateJournal(String id, JournalRequestDTO dto, User user);
    void deleteJournal(String id, User user);
}
