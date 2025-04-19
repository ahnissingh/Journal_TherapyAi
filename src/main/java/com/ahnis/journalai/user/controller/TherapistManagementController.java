package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistPersonalResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.service.TherapistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/therapists/me")
@PreAuthorize("hasRole('ROLE_THERAPIST')")
@RequiredArgsConstructor
public class TherapistManagementController {
    private final TherapistService therapistService;

    @GetMapping
    public ResponseEntity<ApiResponse<TherapistPersonalResponse>> getTherapistProfile(
            @AuthenticationPrincipal Therapist therapist
    ) {
        var profile = therapistService.getProfile(therapist.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody TherapistUpdateRequest request,
            @AuthenticationPrincipal Therapist therapist
    ) {
        therapistService.updateProfile(therapist.getId(), request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(HttpStatus.NO_CONTENT, "Profile updated", null));
    }

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<List<TherapistClientResponse>>> getMyClients(
            @AuthenticationPrincipal Therapist therapist
    ) {
        var clients = therapistService.getClients(therapist.getId());
        return ResponseEntity.ok(ApiResponse.success(clients));
    }
}
