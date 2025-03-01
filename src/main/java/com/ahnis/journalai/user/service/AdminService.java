package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;

import java.util.List;

public interface AdminService {

    //new methods
    List<UserResponse> getAllUsers();

    void enableUser(String userId);

    void disableUser(String userId);

    void lockUser(String userId);

    void unlockUser(String userId);

    void deleteUserById(String userId);

    UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest);

    void registerMultipleUsers(List<UserRegistrationRequest> userRegistrationRequestList);

}
