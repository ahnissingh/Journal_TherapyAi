package com.ahnis.journalai.ai.analysis.service;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.user.entity.Preferences;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface JournalAnalysisService {
    CompletableFuture<MoodReportResponse> analyzeUserMood(String userId, Preferences userPreferences, Instant startDate, Instant endDate);
}
