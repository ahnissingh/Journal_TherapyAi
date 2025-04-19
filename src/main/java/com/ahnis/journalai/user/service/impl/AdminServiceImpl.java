package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.AdminService;
import com.ahnis.journalai.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username")); // Adjust the sorting as needed
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toResponseDto);
    }


    @Override
    public void enableUser(String userId) {
        var status = userRepository.updateEnabledStatus(userId, true);
        validateStatus(userId, status);
    }

    @Override
    public void disableUser(String userId) {
        var status = userRepository.updateEnabledStatus(userId, false);
        validateStatus(userId, status);
    }

    @Override
    public void lockUser(String userId) {
        var status = userRepository.updateAccountNonLockedStatus(userId, false);
        validateStatus(userId, status);
    }

    @Override
    public void unlockUser(String userId) {
        var status = userRepository.updateAccountNonLockedStatus(userId, true);
        validateStatus(userId, status);
    }

    @Override
    public void deleteUserById(String userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("User not found with id", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest) {
        // 1. Fetch the existing user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: ", userId));

        // 2. Update email if provided and changed
        if (userUpdateRequest.email() != null && !userUpdateRequest.email().equals(user.getEmail())) {
            validateEmail(userUpdateRequest.email());
            userRepository.updateEmail(userId, userUpdateRequest.email()); // Use repository method for direct update
            user.setEmail(userUpdateRequest.email()); // Update in-memory entity
        }

        // 3. Update password if provided
        if (userUpdateRequest.password() != null) {
            String encodedPassword = passwordEncoder.encode(userUpdateRequest.password());
            userRepository.updatePassword(userId, encodedPassword); // Use repository method for direct update
            user.setPassword(encodedPassword); // Update in-memory entity
        }

        // 4. Update preferences if provided
        if (userUpdateRequest.preferences() != null) {
            Preferences updatedPreferences = userMapper.toPreferencesEntity(userUpdateRequest.preferences());
            userRepository.updatePreferences(userId, updatedPreferences); // Use repository method for direct update
            user.setPreferences(updatedPreferences); // Update in-memory entity
        }

        // 5. Update audit fields
        user.setUpdatedAt(Instant.now());

        // 6. Save the updated user (if needed)
        var updatedUser = userRepository.save(user);

        // 7. Return the updated DTO
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public void registerMultipleUsers(List<UserRegistrationRequest> userRegistrationRequestList) {
        var users = userRegistrationRequestList.stream()
                .map(dto -> {
                    var user = userMapper.toEntity(dto);
                    user.setPassword(passwordEncoder.encode(dto.password()));
                    user.setRoles(Set.of(Role.USER));
                    if (user.getPreferences() != null && user.getPreferences().getReportFrequency() != null) {
                        var nextReportOn = UserUtils.calculateNextReportOn(Instant.now(), user.getPreferences().getReportFrequency());
                        user.setNextReportOn(nextReportOn);

                    }

                    return user;
                })
                .toList();//immutable list as we don't need to edit this
        userRepository.saveAll(users);
        log.info("Saved bulk users count: {}", users.size());
    }

    private void validateStatus(String userId, long status) {
        if (status == 0)
            throw new UserNotFoundException("User not found", userId);
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyExistsException(email);
    }


}
