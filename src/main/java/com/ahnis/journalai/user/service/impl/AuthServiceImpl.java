package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.common.security.JwtUtil;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.exception.UsernameOrEmailAlreadyExistsException;
import com.ahnis.journalai.user.mapper.TherapistMapper;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.AuthService;
import com.ahnis.journalai.user.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final CaffeineCacheManager cacheManager;
    private final TherapistMapper therapistMapper;


    @Override
    public AuthResponse registerUser(UserRegistrationRequest registrationDTO) {
        //Step1 Validate the request for email or username
        validateRegistration(registrationDTO);
        //Step2 Map request to entity
        User newUser = userMapper.toEntity(registrationDTO);

        //Step3 hash the password and set to entity and set roles
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));
        newUser.setRoles(Set.of(Role.USER)); //Default role for users lmao won't give admin

        //Step4: Calculate nextReportOn based on reportFrequency in preferences

        Instant nextReportOn = UserUtils.calculateNextReportOn(Instant.now(), newUser.getPreferences().getReportFrequency());
        Instant nextReportOnFixedAtStartOfDay = nextReportOn.truncatedTo(ChronoUnit.DAYS);
        newUser.setNextReportOn(nextReportOnFixedAtStartOfDay);
        newUser.setLastReportAt(null);//Newly registered  user has no reports obviously

        //Step5 Convert entity  to object having jwt token
        User savedUser = userRepository.save(newUser);
        log.info("Saving user with username {} \n email {} \n preferences {} \n nextReportOn {}",
                registrationDTO.username(), registrationDTO.email(), registrationDTO.preferences(), newUser.getNextReportOn());
        return buildAuthResponse(savedUser);
    }
//Single login method

    @Override
    @Cacheable(value = "authResponses", key = "#authRequest.usernameOrEmail()")
    public AuthResponse loginUser(AuthRequest authRequest) {
        var cache = cacheManager.getCache("authResponses");
        if (cache != null) {
            log.info("Cache has been cached for authRequest {}", authRequest.usernameOrEmail());
        }
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.usernameOrEmail(),
                        authRequest.password()));
        var user = (UserDetails) authentication.getPrincipal();
        log.info("User logged in {}", authRequest.usernameOrEmail());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse registerTherapist(TherapistRegistrationRequest therapistRegistrationRequest) {
        if (therapistRepository.existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email()) ||
                userRepository.existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email())
        ) throw new UsernameOrEmailAlreadyExistsException(therapistRegistrationRequest.email());

        var therapist = therapistMapper.toEntity(therapistRegistrationRequest);
        therapist.setPassword(passwordEncoder.encode(therapistRegistrationRequest.password()));

        therapistRepository.save(therapist);
        return buildAuthResponse(therapist);
    }


    private AuthResponse buildAuthResponse(UserDetails user) {
        return new AuthResponse(jwtUtil.generateToken(user));
    }

    private void validateRegistration(UserRegistrationRequest dto) {
        if (userRepository.existsByUsernameOrEmail(dto.username(), dto.email()))
            throw new UsernameOrEmailAlreadyExistsException("Username or email already exists {%s , %s }  ".formatted(dto.username(), dto.email()));
    }
}
