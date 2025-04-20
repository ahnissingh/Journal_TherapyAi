package com.ahnis.journalai.user.service.impl;

import com.ahnis.journalai.common.security.JwtUtil;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.request.PreferencesRequest;
import com.ahnis.journalai.user.dto.request.TherapistRegistrationRequest;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.Therapist;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.enums.Gender;
import com.ahnis.journalai.user.enums.Language;
import com.ahnis.journalai.user.enums.ReportFrequency;
import com.ahnis.journalai.user.enums.Role;
import com.ahnis.journalai.user.enums.SupportStyle;
import com.ahnis.journalai.user.enums.ThemePreference;
import com.ahnis.journalai.user.exception.UsernameOrEmailAlreadyExistsException;
import com.ahnis.journalai.user.mapper.UserMapper;
import com.ahnis.journalai.user.repository.TherapistRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private CaffeineCacheManager cacheManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRegistrationRequest userRegistrationRequest;
    private TherapistRegistrationRequest therapistRegistrationRequest;
    private AuthRequest authRequest;
    private User testUser;
    private Therapist testTherapist;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup test data
        PreferencesRequest preferencesRequest = new PreferencesRequest(
            ReportFrequency.WEEKLY,
            Language.ENGLISH,
            ThemePreference.LIGHT,
            SupportStyle.FRIENDLY,
            30,
            Gender.MALE,
            true
        );

        userRegistrationRequest = new UserRegistrationRequest(
            "Test",
            "User",
            "testuser",
            "test@example.com",
            "password123",
            preferencesRequest,
            "America/New_York"
        );

        therapistRegistrationRequest = new TherapistRegistrationRequest(
            "therapist",
            "therapist@example.com",
            "John",
            "Doe",
            5,
            "Professional therapist with experience in anxiety and depression treatment.",
            Set.of(Language.ENGLISH, Language.FRENCH),
            "password123",
            "LIC123456",
            Set.of("Anxiety", "Depression"),
            "https://example.com/profile.jpg"
        );

        authRequest = new AuthRequest("testuser", "password123");

        Preferences preferences = new Preferences();
        preferences.setReportFrequency(ReportFrequency.WEEKLY);

        testUser = new User();
        testUser.setId("user123");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPreferences(preferences);
        testUser.setRoles(Set.of(Role.USER));

        testTherapist = new Therapist();
        testTherapist.setId("therapist123");
        testTherapist.setUsername("therapist");
        testTherapist.setEmail("therapist@example.com");
        testTherapist.setPassword("encodedPassword");
        testTherapist.setLicenseNumber("LIC123456");
        testTherapist.setFirstName("John");
        testTherapist.setLastName("Doe");
        testTherapist.setYearsOfExperience(5);
        testTherapist.setBio("Professional therapist with experience in anxiety and depression treatment.");
        testTherapist.setSpecialties(Set.of("Anxiety", "Depression"));
        testTherapist.setLanguages(Set.of(Language.ENGLISH, Language.FRENCH));
        testTherapist.setProfilePictureUrl("https://example.com/profile.jpg");

        jwtToken = "test.jwt.token";
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_ShouldRegisterSuccessfully() {
        // Given
        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
        when(userMapper.toEntity(userRegistrationRequest)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        // When
        AuthResponse response = authService.registerUser(userRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        verify(userRepository).existsByUsernameOrEmail(userRegistrationRequest.username(), userRegistrationRequest.email());
        verify(userMapper).toEntity(userRegistrationRequest);
        verify(passwordEncoder).encode(userRegistrationRequest.password());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw UsernameOrEmailAlreadyExistsException when username or email already exists")
    void registerUser_ShouldThrowException_WhenUsernameOrEmailExists() {
        // Given
        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

        // When/Then
        assertThrows(UsernameOrEmailAlreadyExistsException.class, () ->
            authService.registerUser(userRegistrationRequest)
        );
        verify(userRepository).existsByUsernameOrEmail(userRegistrationRequest.username(), userRegistrationRequest.email());
        verify(userMapper, never()).toEntity(any(UserRegistrationRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void loginUser_ShouldLoginSuccessfully() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        // When
        AuthResponse response = authService.loginUser(authRequest);

        // Then
        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should register therapist successfully")
    void registerTherapist_ShouldRegisterSuccessfully() {
        // Given
        when(therapistRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(therapistRepository.save(any(Therapist.class))).thenReturn(testTherapist);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        // When
        AuthResponse response = authService.registerTherapist(therapistRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        verify(therapistRepository).existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email());
        verify(userRepository).existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email());
        verify(passwordEncoder).encode(therapistRegistrationRequest.password());
        verify(therapistRepository).save(any(Therapist.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw UsernameOrEmailAlreadyExistsException when therapist username or email already exists")
    void registerTherapist_ShouldThrowException_WhenUsernameOrEmailExists() {
        // Given
        when(therapistRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

        // When/Then
        assertThrows(UsernameOrEmailAlreadyExistsException.class, () ->
            authService.registerTherapist(therapistRegistrationRequest)
        );
        verify(therapistRepository).existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email());
        verify(therapistRepository, never()).save(any(Therapist.class));
    }

    @Test
    @DisplayName("Should throw UsernameOrEmailAlreadyExistsException when user with same username or email exists")
    void registerTherapist_ShouldThrowException_WhenUserWithSameCredentialsExists() {
        // Given
        when(therapistRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

        // When/Then
        assertThrows(UsernameOrEmailAlreadyExistsException.class, () ->
            authService.registerTherapist(therapistRegistrationRequest)
        );
        verify(therapistRepository).existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email());
        verify(userRepository).existsByUsernameOrEmail(therapistRegistrationRequest.username(), therapistRegistrationRequest.email());
        verify(therapistRepository, never()).save(any(Therapist.class));
    }
}
