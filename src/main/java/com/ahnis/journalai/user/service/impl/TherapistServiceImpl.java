package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistProfileResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.ConflictException;
import com.ahnis.journalai.user.exception.UserNotFoundException;
import com.ahnis.journalai.user.mapper.TherapistMapper;
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
import org.springframework.data.mongodb.core.query.Update;
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
    private final TherapistMapper therapistMapper;

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
                .map(therapistMapper::toResponse)
                .toList();
    }


    @Override
    public Page<TherapistResponse> getAllTherapists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return therapistRepository.findAll(pageable)
                .map(therapistMapper::toResponse);

    }

    @Transactional
    @Override
    public void subscribe(String userId, String therapistId) {
        // Step 1: Atomically update user only if not already subscribed
        Query userQuery = new Query(Criteria.where("_id").is(userId)
                .and("therapistId").exists(false));

        Update userUpdate = new Update()
                .set("therapistId", therapistId)
                .set("subscribedAt", Instant.now());

        var userResult = mongoTemplate.updateFirst(userQuery, userUpdate, User.class);

        if (userResult.getMatchedCount() == 0) {
            throw new ConflictException("User already subscribed to therapist, unsubscribe them first");
        }

        // Step 2: Atomically add userId to therapist's client list
        Query therapistQuery = new Query(Criteria.where("_id").is(therapistId));
        Update therapistUpdate = new Update().addToSet("clientUserId", userId);

        mongoTemplate.updateFirst(therapistQuery, therapistUpdate, Therapist.class);

        // Step 3: Optional notification
        // notificationService.sendSubscriptionNotification(therapistId, userId);
    }


    @Override
    public TherapistProfileResponse getProfile(String id) {
        return therapistRepository.findById(id)
                .map(therapistMapper::toPersonalResponse)
                .orElseThrow(() -> new UserNotFoundException("Therapist not found", id));
    }

    @Transactional
    @Override
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
        return clientsPage.map(therapistMapper::toClientResponse);
    }


}
