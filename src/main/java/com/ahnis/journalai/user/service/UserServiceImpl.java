package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.exception.UsernameAlreadyExistsException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationRequest registrationDTO) {
        validateRegistration(registrationDTO);

        User newUser = UserMapper.toEntity(registrationDTO);
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));

        newUser.setRoles(Set.of(Role.USER));
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User currentUser = getAuthenticatedUser();
        return UserMapper.toResponseDto(currentUser);
    }

    @Override //todo note: This is for admin user refactor to reduce duplicacy
    public UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        // Update email if provided and changed
        if (userUpdateRequest.email() != null && !userUpdateRequest.email().equals(user.getEmail())) {
            validateEmail(userUpdateRequest.email());
            user.setEmail(userUpdateRequest.email());
        }

        // Update password if provided
        if (userUpdateRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
        }
        if (userUpdateRequest.preferences() != null) {
            user.setPreferences(UserMapper.toPreferencesEntity(userUpdateRequest.preferences()));
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponse updateCurrentUser(UserUpdateRequest updateDTO) {
        User currentUser = getAuthenticatedUser();
        // Update email if provided and changed
        if (updateDTO.email() != null && !updateDTO.email().equals(currentUser.getEmail())) {
            validateEmail(updateDTO.email());
            currentUser.setEmail(updateDTO.email());
        }

        // Update password if provided
        if (updateDTO.password() != null) {
            currentUser.setPassword(passwordEncoder.encode(updateDTO.password()));
        }
        if (updateDTO.preferences() != null) {
            currentUser.setPreferences(UserMapper.toPreferencesEntity(updateDTO.preferences()));
        }

        User updatedUser = userRepository.save(currentUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponse updateUserPreferences(PreferencesRequest preferencesRequest) {
        User currentUser = getAuthenticatedUser();
        currentUser.setPreferences(UserMapper.toPreferencesEntity(preferencesRequest));

        User updatedUser = userRepository.save(currentUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public void deleteUser() {
        User currentUser = getAuthenticatedUser();
        userRepository.delete(currentUser);
    }

    @Override
    @Transactional(readOnly = true)

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    //new methods
    @Override
    public void enableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(getUsernameNotFoundExceptionSupplier(userId));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(getUsernameNotFoundExceptionSupplier(userId));
        user.setEnabled(false);
        userRepository.save(user);
        log.warn("Disabled user {}", user.getUsername());

    }

    @Override
    public void lockUser(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(getUsernameNotFoundExceptionSupplier(userId));
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Override
    public void unlockUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(getUsernameNotFoundExceptionSupplier(userId));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }


    @Override
    public void deleteUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        userRepository.delete(user);
    }


    // Helper Methods
    private Supplier<UsernameNotFoundException> getUsernameNotFoundExceptionSupplier(String userId) {
        return () -> new UsernameNotFoundException("User not found " + userId);
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(getUsernameNotFoundExceptionSupplier(username));
    }

    private void validateRegistration(UserRegistrationRequest dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistsException(dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
