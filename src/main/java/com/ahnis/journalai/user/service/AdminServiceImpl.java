package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public void enableUser(String userId) {
        long updatedCount = userRepository.updateEnabledStatus(userId, true);
        checkAndThrow(updatedCount, userId);
    }

    private void checkAndThrow(long updatedCount, String userId) {
        if (updatedCount == 0)
            throw new UserNotFoundException("User not found with id ", userId);
    }


    @Override
    public void disableUser(String userId) {
        userRepository.updateEnabledStatus(userId, false);
    }

    @Override
    public void lockUser(String userId) {
        userRepository.updateAccountNonLockedStatus(userId, false);
    }

    @Override
    public void unlockUser(String userId) {
        userRepository.updateAccountNonLockedStatus(userId, true);
    }

    @Override
    public void deleteUserById(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", userId));

        if (userUpdateRequest.email() != null && !userUpdateRequest.email().equals(user.getEmail())) {
            validateEmail(userUpdateRequest.email());
            userRepository.updateEmail(userId, userUpdateRequest.email());
        }

        if (userUpdateRequest.password() != null)
            userRepository.updatePassword(userId, passwordEncoder.encode(userUpdateRequest.password()));

        if (userUpdateRequest.preferences() != null) {
            userRepository.updatePreferences(userId, UserMapper.toPreferencesEntity(userUpdateRequest.preferences()));
        }

        // Fetch the updated user to return the response
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found ", userId));
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    //Crucial step
    //1st when user register (it should be unique)
    //2nd when user updates their email then also it should be unique
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }


}
