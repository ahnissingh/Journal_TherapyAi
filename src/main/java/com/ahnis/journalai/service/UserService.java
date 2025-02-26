package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.user.request.PreferencesRequest;
import com.ahnis.journalai.dto.user.request.UserRegistrationRequest;
import com.ahnis.journalai.dto.user.response.UserResponse;
import com.ahnis.journalai.dto.user.request.UserUpdateRequest;
import com.ahnis.journalai.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationRequest registrationDTO);

    UserResponse getCurrentUser();

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    UserResponse updateUserPreferences(PreferencesRequest preferencesRequest);

    void deleteUser();

    List<UserResponse> getAllUsers(); //ONLY ADMINS WILL USE THIS
}
