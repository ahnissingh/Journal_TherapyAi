package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.UserUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.dto.response.UserResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.exception.EmailAlreadyExistsException;
import com.ahnis.journalai.user.exception.UserNotSubscribedException;
import com.ahnis.journalai.user.mapper.TherapistMapper;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.UserService;
import com.ahnis.journalai.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TherapistRepository therapistRepository;
    private final MongoTemplate mongoTemplate;
    private final TherapistMapper therapistMapper;

    @Transactional
    public void updateUserReportDates(User user, Instant nextReportOn, Instant newNextReportOn) {
        userRepository.updateLastReportAtById(user.getId(), nextReportOn);
        userRepository.updateNextReportOnById(user.getId(), newNextReportOn);
        log.info("LastReportAt and NextReportOn fields updated for user: {}", user.getUsername());
    }

    @Override
    public Preferences getUserPreferencesByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getPreferences)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found " + username));
    }

    @Override
    public TherapistResponse getSubscribedTherapist(String username) {
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                Aggregation.lookup("therapists", "therapistId", "_id", "therapist"),
                Aggregation.unwind("therapist", true),
                Aggregation.project()
                        .and("therapist").as("therapist")
                        .and("therapistId").as("hasTherapist")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "users", Document.class);
        if (results.getMappedResults().isEmpty())
            throw new UsernameNotFoundException("Username not found " + username);
        Document resultDoc = results.getMappedResults().getFirst();

        if (resultDoc.get("hasTherapist") == null)
            throw new UserNotSubscribedException("No subscribed therapist");

        var therapistDoc = resultDoc.get("therapist", Document.class);
        var therapist = mongoTemplate.getConverter().read(
                Therapist.class, therapistDoc
        );
        // Apply bio truncation and mapping
        if (therapist.getBio() != null && therapist.getBio().length() > 100) {
            therapist.setBio(therapist.getBio().substring(0, 100) + "...");
        }
        return therapistMapper.toResponseIgnoreUsername(therapist);
    }


    @Transactional(readOnly = true)
    //OK optimised
    public UserResponse getUserResponseByUsername(String username) {
        return userMapper.toResponseDto(this.getUserByUsername(username));
    }


    @Transactional
    public void updateUserByUsername(String username, UserUpdateRequest updateDTO) {
        // Update email if provided and changed
        var currentUser = getUserByUsername(username);
        if (updateDTO.email() != null && !updateDTO.email().equals(currentUser.getEmail())) {
            validateEmail(updateDTO.email());
            userRepository.updateEmailByUsername(username, updateDTO.email());
        }

        // Update password if provided
        if (updateDTO.password() != null) {
            userRepository.updatePasswordByUsername(username, passwordEncoder.encode(updateDTO.password()));
        }
        // Update preferences if provided
        //todo patch update every property I think can give better perfomance
        var updatedPreferences = updateDTO.preferences();
        if (updatedPreferences != null) {
            if (updatedPreferences.reportFrequency() != null && !updatedPreferences.reportFrequency().equals(currentUser.getPreferences().getReportFrequency())) {
                var nextReportOn = UserUtils.calculateNextReportOn(Instant.now(), updatedPreferences.reportFrequency());
                userRepository.updateNextReportOnById(currentUser.getId(), nextReportOn);
            }
            userRepository.updatePreferencesByUsername(username, userMapper.toPreferencesEntity(updatedPreferences));
        }

    }


    @Transactional
    public void updateUserPreferences(String username, PreferencesRequest preferencesRequest) {
//        long count = userRepository.updatePreferencesByUsername(username, userMapper.toPreferencesEntity(preferencesRequest));
//        if (count == 0) throw new UsernameNotFoundException("Username not found");
        var currentUser = getUserByUsername(username);
        if (!preferencesRequest.reportFrequency().equals(currentUser.getPreferences().getReportFrequency())) {
            var nextReportOn = UserUtils.calculateNextReportOn(Instant.now(), preferencesRequest.reportFrequency());

            userRepository.updateNextReportOnByUsername(username, nextReportOn);
        }
        userRepository.updatePreferencesByUsername(username, userMapper.toPreferencesEntity(preferencesRequest));
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        long deletedCount = userRepository.deleteByUsername(username);
        if (deletedCount == 0) throw new UsernameNotFoundException("Username not found , User not deleted");
    }

    // Helper Methods
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyExistsException(email);
    }
}
