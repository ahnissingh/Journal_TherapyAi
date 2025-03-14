package com.ahnis.journalai.analysis.service;

import com.ahnis.journalai.analysis.dto.MoodReportEmailResponse;
import com.ahnis.journalai.user.entity.Preferences;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface JournalAnalysisService {
    CompletableFuture<MoodReportEmailResponse> analyzeUserMood(String userId, String username, Preferences userPreferences, Instant startDate, Instant endDate);
}
