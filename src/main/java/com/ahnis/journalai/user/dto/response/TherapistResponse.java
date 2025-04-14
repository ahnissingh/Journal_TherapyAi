package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.enums.Language;

import java.util.Set;

public record TherapistResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        Set<String> specialties,
        Set<Language> spokenLanguages,
        int yearsOfExperience,
        String bio,
        String profilePictureUrl
) {
}
