package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateCurrentUser(userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User updated successfully", null));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User deleted successfully", null));
    }


}
