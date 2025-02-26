package com.ahnis.journalai.controller;

import com.ahnis.journalai.dto.common.ApiResponse;
import com.ahnis.journalai.dto.journal.JournalRequest;
import com.ahnis.journalai.dto.journal.JournalResponse;
import com.ahnis.journalai.entity.User;
import com.ahnis.journalai.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

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
}
