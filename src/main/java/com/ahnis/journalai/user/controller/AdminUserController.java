package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")

@Slf4j
@RequiredArgsConstructor
//todo refactor this class to a production grade
//todo this is only for testing
public class AdminUserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping()
    @Async
    public void saveAllUsers(
            @RequestBody List<UserRegistrationRequest> userRegistrationRequestList
    ) {
        userRegistrationRequestList.forEach(userService::registerUser);
    }
}
