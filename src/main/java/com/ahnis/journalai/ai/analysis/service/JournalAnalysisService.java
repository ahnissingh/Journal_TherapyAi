package com.ahnis.journalai.ai.analysis.service;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public interface JournalAnalysisService {
    CompletableFuture<MoodReportResponse> analyzeUserMood(String userId);

    CompletableFuture<MoodReportResponse> analyzeUserMood(String userId, Instant startDate, Instant endDate);
}
