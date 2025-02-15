package com.ahnis.journalai.controller;

import com.ahnis.journalai.dto.ApiResponse;
import com.ahnis.journalai.dto.JournalRequestDTO;
import com.ahnis.journalai.dto.JournalResponseDTO;
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
    public ResponseEntity<ApiResponse<JournalResponseDTO>> createJournal(
            @RequestBody JournalRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        JournalResponseDTO response = journalService.createJournal(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED, "Journal created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalResponseDTO>>> getAllJournals(
            @AuthenticationPrincipal User user
    ) {
        List<JournalResponseDTO> journals = journalService.getAllJournals(user);
        return ResponseEntity.ok(ApiResponse.success(journals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponseDTO>> getJournalById(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        JournalResponseDTO journal = journalService.getJournalById(id, user);
        return ResponseEntity.ok(ApiResponse.success(journal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalResponseDTO>> updateJournal(
            @PathVariable String id,
            @RequestBody JournalRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        JournalResponseDTO updatedJournal = journalService.updateJournal(id, dto, user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.ACCEPTED,"Journal updated successfully", updatedJournal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJournal(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        journalService.deleteJournal(id, user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Journal deleted successfully", null));
    }
}
