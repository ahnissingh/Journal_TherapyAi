package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistPersonalResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.ConflictException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TherapistServiceImpl implements TherapistService {
    private final TherapistRepository therapistRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;


    @Override
    public List<TherapistResponse> search(
            String specialty,
            String username
    ) {
        var criteria = new Criteria();
        if (specialty != null)
            criteria.and("specialties").regex(specialty, "i");
        //todo remove and replace with first name and last name
        if (username != null)
            criteria.and("username").regex(username, "i");
        var query = Query.query(criteria);

        List<Therapist> therapists = mongoTemplate.find(query, Therapist.class);
        return therapists.stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public Page<TherapistResponse> getAllTherapists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return therapistRepository.findAll(pageable)
                .map(this::mapToResponse);

    }

    @Transactional @Override
    public void subscribe(String userId, String therapistId) {
        var therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new UserNotFoundException("Therapist not found", therapistId));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", userId));
        if (user.getTherapistId() != null) {
            throw new ConflictException("User already subscribed to therapist,Unsubscribe them first");
        }

        // Updating both sides of relationship ie one to many
        user.setTherapistId(therapistId);
        user.setSubscribedAt(Instant.now());
        therapist.getClientUserId().add(userId);

        //Todo refactor to atomic update
        userRepository.save(user);
        therapistRepository.save(therapist);

        //notificationService.sendSubscriptionNotification(therapist, user);
    }


    private TherapistResponse mapToResponse(Therapist therapist) {
        return new TherapistResponse(
                therapist.getId(),
                therapist.getUsername(),
                therapist.getFirstName(),
                therapist.getLastName(),
                therapist.getSpecialties(),
                therapist.getLanguages(),
                therapist.getYearsOfExperience(),
                therapist.getBio(),
                therapist.getProfilePictureUrl()
        );
    }

    @Override
    public TherapistPersonalResponse getProfile(String id) {
        return therapistRepository.findById(id)
                .map(TherapistPersonalResponse::fromEntity)
                .orElseThrow(() -> new UserNotFoundException("Therapist not found", id));
    }

    @Transactional @Override
    public void updateProfile(String therapistId, TherapistUpdateRequest request) {
        Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new UserNotFoundException("Therapist not found", therapistId));

        therapist.setBio(request.bio());
        therapist.setSpecialties(request.specialties());
        therapist.setLanguages(request.spokenLanguages());
        therapist.setYearsOfExperience(request.yearsOfExperience());
        therapist.setProfilePictureUrl(request.profilePictureUrl());

        therapistRepository.save(therapist);
    }

    @Override
    public Page<TherapistClientResponse> getClients(Set<String> clientUserIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Create a pageable object with given page and size

        // Fetch the clients using the `clientUserIds` set and `Pageable` object
        Page<User> clientsPage = userRepository.findAllByIdIn(clientUserIds, pageable);

        // Map the page of users to a page of TherapistClientResponse
        return clientsPage.map(TherapistClientResponse::fromUser);
    }



}
