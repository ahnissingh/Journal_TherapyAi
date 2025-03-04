package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;

public interface UserService {


    UserResponse getCurrentUser();

    void updateCurrentUser(UserUpdateRequest userUpdateRequest);

    void updateUserPreferences(PreferencesRequest preferencesRequest);

    void deleteCurrentUser();


}
