package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserResponseByUsername(String username);

    void updateUserByUsername(String username, UserUpdateRequest updateDTO);

    void updateUserPreferences(String username, PreferencesRequest preferencesRequest);

    void deleteUserByUsername(String username);
}
