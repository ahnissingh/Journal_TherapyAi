package com.ahnis.journalai.controller;

import com.ahnis.journalai.dto.common.ApiResponse;
import com.ahnis.journalai.dto.user.response.UserResponse;
import com.ahnis.journalai.dto.user.request.UserUpdateRequest;
import com.ahnis.journalai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        var currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User updated successfully", updatedUser)); // Using ApiResponse with message
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User deleted successfully", null)); // Response with NO_CONTENT status
    }

    @PreAuthorize("hasRole('ADMIN')") //issue with preAuthorize using basic if statement
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        userService.getCurrentUser().roles().forEach(role -> log.warn(String.valueOf(role)));
//        if (!userService.getCurrentUser().roles().contains(Role.ADMIN))
//            return ResponseEntity.ok(ApiResponse.success(HttpStatus.FORBIDDEN, "You must be ADMIN to get all users", null));
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users)); //todo return 403 forbidden
    }

}
