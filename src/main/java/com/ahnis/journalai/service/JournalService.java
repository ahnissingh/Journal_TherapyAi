package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.journal.JournalRequest;
import com.ahnis.journalai.dto.journal.JournalResponse;

import java.util.List;

public interface JournalService {
    JournalResponse createJournal(JournalRequest dto, String userId);
    List<JournalResponse> getAllJournals(String userId);
    JournalResponse getJournalById(String id, String userId);
    JournalResponse updateJournal(String id, JournalRequest dto, String userId);
    void deleteJournal(String id, String userId);
}
