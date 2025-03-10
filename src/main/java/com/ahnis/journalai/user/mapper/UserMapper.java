package com.ahnis.journalai.user.mapper;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import org.mapstruct.*;

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
    @Mapping(target = "nextReportOn", ignore = true)
    @Mapping(target = "currentStreak", ignore = true) // Ignored during registration
    @Mapping(target = "longestStreak", ignore = true) // Ignored during registration
    @Mapping(target = "lastJournalEntryDate", ignore = true)

        // Ignored during registration
        //nextReportOn is calculated in service
    User toEntity(UserRegistrationRequest dto);

    // Preferences -> PreferencesRequest
    PreferencesRequest toPreferencesDto(Preferences preferences);

    Preferences toPreferencesEntity(PreferencesRequest preferencesRequest);

    //    User -> UserResponse
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "preferences", source = "preferences")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "nextReportOn", source = "nextReportOn")
    @Mapping(target = "currentStreak", source = "currentStreak")
    @Mapping(target = "longestStreak", source = "longestStreak")
    @Mapping(target = "lastJournalEntryDate", source = "lastJournalEntryDate")
    UserResponse toResponseDto(User user);


}
