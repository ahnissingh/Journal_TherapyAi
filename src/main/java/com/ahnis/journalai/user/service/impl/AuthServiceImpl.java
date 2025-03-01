package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.common.security.JwtUtil;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.exception.UsernameOrEmailAlreadyExistsException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.UserRepository;
import com.ahnis.journalai.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    public AuthResponse registerUser(UserRegistrationRequest registrationDTO) {
        //Step1 Validate the request for email or username
        validateRegistration(registrationDTO);
        //Step2 Map request to entity
        User newUser = userMapper.toEntity(registrationDTO);

        //Step3 hash the password and set to entity and set roles
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));
        newUser.setRoles(Set.of(Role.USER)); //Default role for users lmao wont give admin

        //Step4 Convert entity back to response object (hides password and if other sensitive fields, scalable approach)
        User savedUser = userRepository.save(newUser);
        log.info("Saving user with username {} \n email {} \n preferences {} \n", registrationDTO.username(), registrationDTO.email(), registrationDTO.preferences());
        return buildAuthResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.usernameOrEmail(),
                        authRequest.password()));
        var user = (User) authentication.getPrincipal();
        log.info("User logged in {}", authRequest.usernameOrEmail());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(jwtUtil.generateToken(user));
    }

    private void validateRegistration(UserRegistrationRequest dto) {
        if (userRepository.existsByUsernameOrEmail(dto.username(), dto.email()))
            throw new UsernameOrEmailAlreadyExistsException("Username or email already exists {%s , %s }  ".formatted(dto.username(), dto.email()));
    }
}
