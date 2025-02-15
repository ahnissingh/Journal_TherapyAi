package com.ahnis.journalai.mapper;

// UserMapper.java

import com.ahnis.journalai.dto.PreferencesDTO;
import com.ahnis.journalai.dto.UserRegistrationDTO;
import com.ahnis.journalai.dto.UserResponseDTO;
import com.ahnis.journalai.entity.Preferences;
import com.ahnis.journalai.entity.User;
import com.ahnis.journalai.enums.Language;
import com.ahnis.journalai.enums.ThemePreference;
import com.ahnis.journalai.enums.TherapistType;
import com.ahnis.journalai.enums.TherapyFrequency;

public class UserMapper {

    public static User toEntity(UserRegistrationDTO dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .preferences(toPreferencesEntity(dto.preferences()))
                .build();
    }

    public static Preferences toPreferencesEntity(PreferencesDTO dto) {
        return Preferences.builder()
                .therapyFrequency(TherapyFrequency.valueOf(dto.therapyFrequency().name()))
                .language(Language.valueOf(dto.language().name()))
                .themePreference(ThemePreference.valueOf(dto.themePreference().name()))
                .therapistType(TherapistType.valueOf(dto.therapistType().name()))
                .build();
    }

    public static PreferencesDTO toPreferencesDto(Preferences preferences) {
        return new PreferencesDTO(
                TherapyFrequency.valueOf(preferences.getTherapyFrequency().name()),
                Language.valueOf(preferences.getLanguage().name()),
                ThemePreference.valueOf(preferences.getThemePreference().name()),
                TherapistType.valueOf(preferences.getTherapistType().name())
        );
    }

    public static UserResponseDTO toResponseDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                toPreferencesDto(user.getPreferences()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
