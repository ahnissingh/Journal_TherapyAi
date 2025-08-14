package com.ahnis.journalai.user.mapper;

import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistProfileResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface TherapistMapper {

    @Mapping(target = "username", ignore = true) // don’t expose username
    TherapistResponse toResponseIgnoreUsername(Therapist therapist);

    TherapistResponse toResponse(Therapist therapist);

    TherapistClientResponse toClientResponse(User user);

    TherapistProfileResponse toPersonalResponse(Therapist therapist);

    @Mapping(target = "password", ignore = true)//Password is encrypted and then set
    Therapist toEntity(TherapistRegistrationRequest request);
}
