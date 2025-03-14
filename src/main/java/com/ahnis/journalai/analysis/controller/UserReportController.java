package com.ahnis.journalai.analysis.controller;

import com.ahnis.journalai.analysis.dto.MoodReportApiResponse;
import com.ahnis.journalai.analysis.service.ReportService;
import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class UserReportController {
    private final ReportService reportService;

    // View all reports for the authenticated user
    @GetMapping
    public ApiResponse<List<MoodReportApiResponse>> getAllReports(@AuthenticationPrincipal User user) {
        List<MoodReportApiResponse> reports = reportService.getAllReportsByUserId(user.getId());
        return ApiResponse.success(reports);
    }

    // View a specific report for the authenticated user
    @GetMapping("/{reportId}")
    public ApiResponse<MoodReportApiResponse> getReportById(
            @PathVariable String reportId,
            @AuthenticationPrincipal User user
    ) {
        MoodReportApiResponse report = reportService.getReportById(user.getId(), reportId);
        return ApiResponse.success(report);
    }
    // View the latest report for the authenticated user
    @GetMapping("/latest")
    public ApiResponse<MoodReportApiResponse> getLatestReport(@AuthenticationPrincipal User user) {
        MoodReportApiResponse report = reportService.getLatestReportByUserId(user.getId());
        return ApiResponse.success(report);
    }
}
