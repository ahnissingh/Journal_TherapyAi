package com.ahnis.journalai.service;

import com.ahnis.journalai.dto.PreferencesDTO;
import com.ahnis.journalai.dto.UserRegistrationDTO;
import com.ahnis.journalai.dto.UserResponseDTO;
import com.ahnis.journalai.dto.UserUpdateDTO;
import com.ahnis.journalai.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationDTO registrationDTO);

    UserResponseDTO getCurrentUser();

    UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO);

    UserResponseDTO updateUserPreferences(PreferencesDTO preferencesDTO);

    void deleteUser();

    List<UserResponseDTO> getAllUsers(); //ONLY ADMINS WILL USE THIS
}
