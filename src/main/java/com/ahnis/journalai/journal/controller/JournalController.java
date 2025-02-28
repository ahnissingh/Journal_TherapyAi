package com.ahnis.journalai.journal.controller;

import com.ahnis.journalai.ai.analysis.JournalAnalysisService;
import com.ahnis.journalai.ai.analysis.MoodReport;
import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.journal.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;
    private final MilvusVectorStore vectorStore;
    private final OpenAiChatModel openAiChatModel;
    private final JournalAnalysisService journalAnalysisService;

    @PostMapping
    public ResponseEntity<ApiResponse<JournalResponse>> createJournal(
            @RequestBody JournalRequest dto,
            @AuthenticationPrincipal User user
    ) {
        JournalResponse response = journalService.createJournal(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED, "Journal created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalResponse>>> getAllJournals(
            @AuthenticationPrincipal User user
    ) {
        List<JournalResponse> journals = journalService.getAllJournals(user.getId());
        return ResponseEntity.ok(ApiResponse.success(journals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> getJournalById(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        JournalResponse journal = journalService.getJournalById(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(journal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponse>> updateJournal(
            @PathVariable String id,
            @RequestBody JournalRequest dto,
            @AuthenticationPrincipal User user
    ) {
        JournalResponse updatedJournal = journalService.updateJournal(id, dto, user.getId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.ACCEPTED, "Journal updated successfully", updatedJournal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJournal(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        journalService.deleteJournal(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Journal deleted successfully", null));
    }

    @GetMapping("/mood")
    @Async
    public CompletableFuture<MoodReport> analyse(@AuthenticationPrincipal User user) {
        return journalAnalysisService.analyzeUserMood2(user.getId());
    }

}
