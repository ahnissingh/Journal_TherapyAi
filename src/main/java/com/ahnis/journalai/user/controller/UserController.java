package com.ahnis.journalai.user.controller;


import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.service.UserService;
import com.ahnis.journalai.user.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        var currentUser = userService.getUserResponseByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUserByUsername(userDetails.getUsername(), userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User updated successfully", null));
    }

    //Only changes preferences? then come here
    @PutMapping("/me/preferences")
    public ResponseEntity<ApiResponse<Void>> updateUserPreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PreferencesRequest preferencesUpdateRequest) {
        userService.updateUserPreferences(userDetails.getUsername(), preferencesUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User  Preferences updated successfully", null));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "User deleted successfully", null));
    }

    @GetMapping("/me/therapist")
    public ResponseEntity<ApiResponse<TherapistResponse>> getMyTherapist(
            @AuthenticationPrincipal UserDetails userDetails) {
        var therapist = userService.getSubscribedTherapist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(therapist));
    }

}
