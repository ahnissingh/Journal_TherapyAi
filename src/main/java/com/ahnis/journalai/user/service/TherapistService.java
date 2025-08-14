package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.dto.request.TherapistUpdateRequest;
import com.ahnis.journalai.user.dto.response.TherapistClientResponse;
import com.ahnis.journalai.user.dto.response.TherapistProfileResponse;
import com.ahnis.journalai.user.dto.response.TherapistResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface TherapistService {
    List<TherapistResponse> search(
            String specialty,
            String username
    );

    Page<TherapistResponse> getAllTherapists(int page, int size);

    @Transactional
    void subscribe(String userId, String therapistId);

    TherapistProfileResponse getProfile(String id);

    @Transactional
    void updateProfile(String therapistId, TherapistUpdateRequest request);

    Page<TherapistClientResponse> getClients(Set<String> clientUserIds, int page, int size);
}
