package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.JournalRequestDTO;
import com.ahnis.journalai.dto.JournalResponseDTO;
import com.ahnis.journalai.entity.User;

import java.util.List;

public interface JournalService {
    JournalResponseDTO createJournal(JournalRequestDTO dto, String userId);
    List<JournalResponseDTO> getAllJournals(String userId);
    JournalResponseDTO getJournalById(String id, String userId);
    JournalResponseDTO updateJournal(String id, JournalRequestDTO dto, String userId);
    void deleteJournal(String id, String userId);
}
