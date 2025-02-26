package com.ahnis.journalai.user.mapper;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.*;

public class UserMapper {

    public static User toEntity(UserRegistrationRequest dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .preferences(toPreferencesEntity(dto.preferences()))
                .build();
    }

    public static Preferences toPreferencesEntity(PreferencesRequest dto) {
        return Preferences.builder()
                .therapyFrequency(TherapyFrequency.valueOf(dto.therapyFrequency().name()))
                .language(Language.valueOf(dto.language().name()))
                .themePreference(ThemePreference.valueOf(dto.themePreference().name()))
                .supportStyle(SupportStyle.valueOf(dto.supportStyle().name()))

                .age(dto.age())
                .gender(dto.gender())
                .build();
    }

    public static PreferencesRequest toPreferencesDto(Preferences preferences) {
        return new PreferencesRequest(
                TherapyFrequency.valueOf(preferences.getTherapyFrequency().name()),
                Language.valueOf(preferences.getLanguage().name()),
                ThemePreference.valueOf(preferences.getThemePreference().name()),
                SupportStyle.valueOf(preferences.getSupportStyle().name()),
                preferences.getAge(),
                Gender.valueOf(preferences.getGender().name())
        );
    }

    public static UserResponse toResponseDto(User user) {
        return new UserResponse(
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
