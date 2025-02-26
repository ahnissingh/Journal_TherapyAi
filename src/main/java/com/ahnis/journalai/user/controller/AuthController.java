package com.ahnis.journalai.user.controller;
import com.ahnis.journalai.user.dto.request.AuthRequest;
import com.ahnis.journalai.user.dto.response.AuthResponse;
import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.common.security.JwtUtil;
import com.ahnis.journalai.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usernameOrEmail(),
                        request.password()
                )
        );
        var user = (User) authentication.getPrincipal();


        return ResponseEntity.ok(buildAuthResponse(user));
    }
    //todo remove after testing
    private void logAuthDetails(String username, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        // Get User-Agent (Device Info)
        String userAgent = request.getHeader("User-Agent");

        // Detect if request is from Mobile or Web
        boolean isMobile = userAgent != null && userAgent.matches(".*(Android|iPhone|iPad|Mobile).*");

        log.info("Auth Request came for {} from IP: {}, User-Agent: {}, Device Type: {}",
                username, ipAddress, userAgent, isMobile ? "Mobile" : "Web");

    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest dto) {
        User registeredUser = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildAuthResponse(registeredUser));
    }

    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(
                jwtUtil.generateToken(user)
        );
    }
}
