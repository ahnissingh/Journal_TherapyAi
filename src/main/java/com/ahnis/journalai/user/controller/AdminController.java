package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        //directly get from repo no mapping
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> createUsers(@RequestBody List<UserRegistrationRequest> userRegistrationRequests) {
        userRegistrationRequests.forEach(userService::registerUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Users created successfully", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User deleted successfully", null));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        UserResponse updatedUser = userService.updateUserById(userId, userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User updated successfully", updatedUser));
    }


    @PostMapping("/users/{userId}/enable")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable String userId) {
        userService.enableUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User enabled successfully", null));
    }

    @PostMapping("/users/{userId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable String userId) {
        userService.disableUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User disabled successfully", null));
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable String userId) {
        userService.lockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User locked successfully", null));
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable String userId) {
        userService.unlockUser(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User unlocked successfully", null));
    }
}
