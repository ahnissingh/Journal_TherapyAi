package com.ahnis.journalai.user.mapper;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import org.mapstruct.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    UserResponse toResponseDto(User user);

//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "username", source = "username")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "roles", source = "roles")
//    @Mapping(target = "preferences", source = "preferences")
//    @Mapping(target = "createdAt", expression = "java(convertInstantToZonedDateTime(user.getCreatedAt(), timezone))")
//    @Mapping(target = "updatedAt", expression = "java(convertInstantToZonedDateTime(user.getUpdatedAt(), timezone))")
//    @Mapping(target = "nextReportOn", expression = "java(convertInstantToZonedDateTime(user.getNextReportOn(), timezone))")
//    @Mapping(target = "lastReportAt", expression = "java(user.getLastReportAt() == null ? null : convertInstantToZonedDateTime(user.getLastReportAt(), timezone))")
//    UserResponse toResponseDto(User user, @Context String timezone);
//
//    default ZonedDateTime convertInstantToZonedDateTime(Instant instant, String timezone) {
//        if (instant == null || timezone == null) {
//            return null;
//        }
//        return instant.atZone(ZoneId.of(timezone));
//    }

}
