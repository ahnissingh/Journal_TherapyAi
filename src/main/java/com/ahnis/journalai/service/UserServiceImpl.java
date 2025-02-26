package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.user.request.PreferencesRequest;
import com.ahnis.journalai.dto.user.request.UserRegistrationRequest;
import com.ahnis.journalai.dto.user.response.UserResponse;
import com.ahnis.journalai.dto.user.request.UserUpdateRequest;
import com.ahnis.journalai.entity.User;
import com.ahnis.journalai.enums.Role;
import com.ahnis.journalai.exception.custom.EmailAlreadyExistsException;
import com.ahnis.journalai.exception.custom.UserNotFoundException;
import com.ahnis.journalai.exception.custom.UsernameAlreadyExistsException;
import com.ahnis.journalai.mapper.UserMapper;
import com.ahnis.journalai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

    @Override
    public UserResponse updateUser(UserUpdateRequest updateDTO) {
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

    // Helper Methods
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
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
