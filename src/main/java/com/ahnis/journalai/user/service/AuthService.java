package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerUser(UserRegistrationRequest registrationDTO);

    AuthResponse loginUser(AuthRequest authRequest);

    AuthResponse registerTherapist(TherapistRegistrationRequest registrationDTO);
}
