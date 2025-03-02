package com.ahnis.journalai.ai.analysis.service;

import com.ahnis.journalai.ai.analysis.dto.MoodReportResponse;

import java.util.concurrent.CompletableFuture;

public interface JournalAnalysisService {
    CompletableFuture<MoodReportResponse> analyzeUserMood(String userId);
}
