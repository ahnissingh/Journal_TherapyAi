package com.ahnis.journalai.journal.service;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JournalService {
    void createJournal(JournalRequest dto, String userId);
    Page<JournalResponse> getAllJournals(String userId, int page, int size);

    JournalResponse getJournalById(String id, String userId);
    JournalResponse updateJournal(String id, JournalRequest dto, String userId);
    void deleteJournal(String id, String userId);
}
