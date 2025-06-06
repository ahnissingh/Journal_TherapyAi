package com.ahnis.journalai.analysis.controller;

import com.ahnis.journalai.analysis.dto.MoodReportApiResponse;
import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class UserReportController {
    private final ReportService reportService;

    // View all reports for the authenticated user
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MoodReportApiResponse>>> getAllReports(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MoodReportApiResponse> reports = reportService.getAllReportsByUserId(user.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }


    // View a specific report for the authenticated user
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<MoodReportApiResponse>> getReportById(
            @PathVariable String reportId,
            @AuthenticationPrincipal User user
    ) {
        MoodReportApiResponse report = reportService.getReportById(user.getId(), reportId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    // View the latest report for the authenticated user
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<MoodReportApiResponse>> getLatestReport(
            @AuthenticationPrincipal User user) {
        MoodReportApiResponse report = reportService.getLatestReportByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
