package com.ahnis.journalai.dto.common;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        HttpStatus status, // HTTP status code
        String message, // Description of the response
        LocalDateTime timestamp, // Timestamp of the response
        T data // Actual payload
) {
    public ApiResponse(HttpStatus status, String message, T data) {
        this(status, message, LocalDateTime.now(), data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "Success", data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
