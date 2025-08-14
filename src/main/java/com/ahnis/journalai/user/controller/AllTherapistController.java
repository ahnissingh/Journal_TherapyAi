package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/therapists")
@RequiredArgsConstructor
public class AllTherapistController {
    private final TherapistService therapistServiceImpl;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TherapistResponse>>> getAllTherapists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var therapists = therapistServiceImpl.getAllTherapists(page, size);
        return ResponseEntity.ok(ApiResponse.success(therapists));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TherapistResponse>>> searchTherapists(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String username
    ) {
        var results = therapistServiceImpl.search(specialty, username);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping("/{therapistId}/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(
            @PathVariable String therapistId,
            @AuthenticationPrincipal User user
    ) {
        therapistServiceImpl.subscribe(user.getId(), therapistId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Subscription successful", null));
    }
}
