package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;

import java.time.Instant;

public interface UserService {
    UserResponse getUserResponseByUsername(String username);

    void updateUserByUsername(String username, UserUpdateRequest updateDTO);

    void updateUserPreferences(String username, PreferencesRequest preferencesRequest);

    void deleteUserByUsername(String username);

    void updateUserReportDates(User user, Instant nextReportOn, Instant newNextReportOn);

    Preferences getUserPreferencesByUsername(String username);
}
