package com.ahnis.journalai.user.mapper;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "preferences", source = "preferences")
    User toEntity(UserRegistrationRequest dto);

    // Preferences -> PreferencesRequest
    PreferencesRequest toPreferencesDto(Preferences preferences);

    Preferences toPreferencesEntity(PreferencesRequest preferencesRequest);

    //User -> UserResponse
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "preferences", source = "preferences")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toResponseDto(User user);

    default ReportFrequency mapReportFrequency(String value) {
        return ReportFrequency.valueOf(value);
    }

    default Language mapLanguage(String value) {
        return Language.valueOf(value);
    }

    default ThemePreference mapThemePreference(String value) {
        return ThemePreference.valueOf(value);
    }

    default SupportStyle mapSupportStyle(String value) {
        return SupportStyle.valueOf(value);
    }

    default Gender mapGender(String value) {
        return Gender.valueOf(value);
    }

}
