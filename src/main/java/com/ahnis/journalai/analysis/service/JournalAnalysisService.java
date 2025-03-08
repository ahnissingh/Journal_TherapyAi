package com.ahnis.journalai.analysis.service;

import com.ahnis.journalai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.user.entity.Preferences;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface JournalAnalysisService {
    CompletableFuture<MoodReportResponse> analyzeUserMood(String userId, String username, Preferences userPreferences, Instant startDate, Instant endDate);
}
