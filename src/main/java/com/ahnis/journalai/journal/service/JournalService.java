package com.ahnis.journalai.journal.service;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;

import java.util.List;

public interface JournalService {
    JournalResponse createJournal(JournalRequest dto, String userId);
    List<JournalResponse> getAllJournals(String userId);
    JournalResponse getJournalById(String id, String userId);
    JournalResponse updateJournal(String id, JournalRequest dto, String userId);
    void deleteJournal(String id, String userId);
}
