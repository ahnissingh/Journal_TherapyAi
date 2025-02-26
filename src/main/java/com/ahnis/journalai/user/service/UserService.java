package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationRequest registrationDTO);

    UserResponse getCurrentUser();

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    UserResponse updateUserPreferences(PreferencesRequest preferencesRequest);

    void deleteUser();

    List<UserResponse> getAllUsers(); //ONLY ADMINS WILL USE THIS
}
