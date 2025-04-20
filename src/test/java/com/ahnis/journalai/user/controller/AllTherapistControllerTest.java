package com.ahnis.journalai.user.controller;

import com.ahnis.journalai.user.dto.response.TherapistResponse;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.service.TherapistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AllTherapistController.class)
class AllTherapistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TherapistService therapistService;

    private User userDetails;
    private TherapistResponse therapistResponse;
    private Page<TherapistResponse> therapistsPage;
    private List<TherapistResponse> therapistsList;

    @BeforeEach
    void setUp() {
        // Setup mock user
        userDetails = new User();
        userDetails.setId("507f1f77bcf86cd799439011");
        userDetails.setUsername("testuser");
        userDetails.setEmail("test@example.com");
        userDetails.setRoles(Set.of(Role.USER));

        // Setup therapist response
        therapistResponse = new TherapistResponse(
                "507f1f77bcf86cd799439012",
                "therapist",
                "John",
                "Doe",
                Set.of("Anxiety", "Depression"),
                Set.of(Language.ENGLISH, Language.FRENCH),
                5,
                "Professional therapist with experience in anxiety and depression treatment.",
                "https://example.com/profile.jpg"
        );

        // Setup therapists page
        therapistsPage = new PageImpl<>(List.of(therapistResponse));

        // Setup therapists list
        therapistsList = List.of(therapistResponse);
    }

    @Test
    @DisplayName("Should get all therapists successfully")
    void getAllTherapists_ShouldReturnTherapists() throws Exception {
        // Given
        when(therapistService.getAllTherapists(anyInt(), anyInt())).thenReturn(therapistsPage);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(therapistResponse.id()))
                .andExpect(jsonPath("$.data.content[0].firstName").value(therapistResponse.firstName()))
                .andExpect(jsonPath("$.data.content[0].lastName").value(therapistResponse.lastName()));
    }

    @Test
    @DisplayName("Should search therapists successfully")
    void searchTherapists_ShouldReturnTherapists() throws Exception {
        // Given
        when(therapistService.search(anyString(), anyString())).thenReturn(therapistsList);

        // When/Then
        mockMvc.perform(get("/api/v1/therapists/search")
                        .param("specialty", "Anxiety")
                        .param("username", "therapist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(therapistResponse.id()))
                .andExpect(jsonPath("$.data[0].firstName").value(therapistResponse.firstName()))
                .andExpect(jsonPath("$.data[0].lastName").value(therapistResponse.lastName()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @DisplayName("Should subscribe to therapist successfully")
    void subscribe_ShouldSubscribeToTherapist() throws Exception {
        // Given
        String therapistId = "507f1f77bcf86cd799439012";
        doNothing().when(therapistService).subscribe(eq(userDetails.getId()), eq(therapistId));

        // When/Then
        mockMvc.perform(post("/api/v1/therapists/{therapistId}/subscribe", therapistId)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subscription successful"));
    }
}
