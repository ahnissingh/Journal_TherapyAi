package com.ahnis.journalai.journal.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.journal.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createJournal(
            @Valid @RequestBody JournalRequest dto,
            @AuthenticationPrincipal User user
    ) {
        journalService.createJournal(dto, user.getId());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Posted Journal", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JournalResponse>>> getAllJournals(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<JournalResponse> journals = journalService.getAllJournals(user.getId(), page, size);
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
            @Valid @RequestBody JournalRequest dto,
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
