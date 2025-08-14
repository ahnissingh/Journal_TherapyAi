package com.ahnis.journalai.user.dto.response;

import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.enums.Language;

import java.time.Instant;
import java.util.Set;

public record TherapistProfileResponse(
        String id,
        String username,
        String email,
        String firstname,
        String lastName,
        String licenseNumber,
        Set<String> specialties,
        Set<Language> languages,
        int yearsOfExperience,
        String bio,
        String profilePictureUrl,
        int clientCount,
        Instant createdAt
) {
    public static TherapistProfileResponse fromEntity(Therapist therapist) {
        return new TherapistProfileResponse(
                therapist.getId(),
                therapist.getUsername(),
                therapist.getEmail(),
                therapist.getFirstName(),
                therapist.getLastName(),
                therapist.getLicenseNumber(),
                therapist.getSpecialties(),
                therapist.getLanguages(),
                therapist.getYearsOfExperience(),
                therapist.getBio(),
                therapist.getProfilePictureUrl(),
                therapist.getClientUserId().size(),
                therapist.getCreatedAt()
        );
    }
}
