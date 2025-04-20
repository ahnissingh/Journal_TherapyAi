package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.AdminService;
import com.ahnis.journalai.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserResponse> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }


    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> createUsers(@RequestBody List<UserRegistrationRequest> userRegistrationRequests) {
        userRegistrationRequests.forEach(authService::registerUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Users created successfully", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        adminService.deleteUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User deleted successfully", null));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        UserResponse updatedUser = adminService.updateUserById(userId, userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User updated successfully", updatedUser));
    }


    @PostMapping("/users/{userId}/enable")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable String userId) {
        adminService.enableUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User enabled successfully", null));
    }

    @PostMapping("/users/{userId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable String userId) {
        adminService.disableUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User disabled successfully", null));
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable String userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User locked successfully", null));
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable String userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User unlocked successfully", null));
    }
}
