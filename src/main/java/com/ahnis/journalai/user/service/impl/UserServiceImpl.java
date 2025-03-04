package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return userMapper.toResponseDto(getAuthenticatedUser());
    }

    //    @Override //todo note: This is for admin user refactor to reduce duplicacy
    //    public UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest) {
    //        User user = userRepository.findById(userId)
    //                .orElseThrow(() -> new UserNotFoundException("User not found: ", userId));
//
    //        // Update email if provided and changed
    //        if (userUpdateRequest.email() != null && !userUpdateRequest.email().equals(user.getEmail())) {
    //            validateEmail(userUpdateRequest.email());
//            user.setEmail(userUpdateRequest.email());
//        }
//
//        // Update password if provided
//        if (userUpdateRequest.password() != null) {
//            user.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
//        }
//        if (userUpdateRequest.preferences() != null) {
//            user.setPreferences(userMapper.toPreferencesEntity(userUpdateRequest.preferences()));
//        }
//
//        User updatedUser = userRepository.save(user);
//        return userMapper.toResponseDto(updatedUser);
//
//    }

    @Override
    public void updateCurrentUser(UserUpdateRequest updateDTO) {
        User currentUser = getAuthenticatedUser();
        // Update email if provided and changed
        if (updateDTO.email() != null && !updateDTO.email().equals(currentUser.getEmail())) {
            validateEmail(updateDTO.email());
            userRepository.updateEmail(currentUser.getId(), updateDTO.email());
        }
        // Update password if provided
        if (updateDTO.password() != null) {
            userRepository.updatePassword(currentUser.getId(), passwordEncoder.encode(updateDTO.password()));
        }
        if (updateDTO.preferences() != null) {
            userRepository.updatePreferences(currentUser.getId(), userMapper.toPreferencesEntity(updateDTO.preferences()));
        }
    }

    @Override
    public void updateUserPreferences(PreferencesRequest preferencesRequest) {
        User currentUser = getAuthenticatedUser();

        userRepository.updatePreferences(currentUser.getId(), userMapper.toPreferencesEntity(preferencesRequest));
    }

    @Override
    public void deleteCurrentUser() {
        var currentUserId = getAuthenticatedUser().getId();
        userRepository.deleteById(currentUserId);
    }


    //new methods

    // Helper Methods
//    @Cacheable(cacheNames = "authUser")
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }


    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyExistsException(email);
    }

}
