//
//package com.ahnis.journalai.common.security.jwt;
//
//import com.ahnis.journalai.common.dto.ErrorDetails;
//import com.ahnis.journalai.common.security.JwtUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtUtil jwtUtil;
//    private final UserDetailsService userDetailsService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException, ExpiredJwtException {
//        try {
//            final var authHeader = request.getHeader("Authorization");
//
//            // Skip if no Bearer token is present
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // Extract JWT and username
//            final var jwt = authHeader.substring(7);
//            final var username = jwtUtil.extractUsername(jwt);
//            log.info("JWT token {}", jwt);
//            log.info("Jwt username {}", username);
//            // Authenticate if username is valid and no existing authentication
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//                // Validate token and set authentication
//                if (jwtUtil.validateToken(jwt, userDetails)) {
//                    var authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities() // Include roles from UserDetails
//                    );
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                    log.info("User authenticated {} ", username);
//                }
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (ExpiredJwtException ex) {
//            var error = new ErrorDetails(
//                    LocalDateTime.now(),
//                    "Token expired",
//                    ex.getMessage());
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            objectMapper.writeValue(response.getWriter(), error);
//            return;
//        } catch (JwtException | AuthenticationException ex) {
//
//            var error = new ErrorDetails(
//                    LocalDateTime.now(),
//                    "Invalid token. Recheck your JWT token",
//                    ex.getMessage()
//            );
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            objectMapper.writeValue(response.getWriter(), error);
//            return;
//        }
//
//    }
//}
package com.ahnis.journalai.common.security.jwt;

import com.ahnis.journalai.common.dto.ErrorDetails;
import com.ahnis.journalai.common.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    // Add this to save the security context for asynchronous processing
    private final RequestAttributeSecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, ExpiredJwtException {
        try {
            final var authHeader = request.getHeader("Authorization");

            // Skip if no Bearer token is present
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract JWT and username
            final var jwt = authHeader.substring(7);
            final var username = jwtUtil.extractUsername(jwt);
            log.info("JWT token {}", jwt);
            log.info("Jwt username {}", username);

            // Authenticate if username is valid and no existing authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token and set authentication
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // Include roles from UserDetails
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the security context
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);

                    // Save the security context for asynchronous processing
                    securityContextRepository.saveContext(context, request, response);

                    log.info("User authenticated {} ", username);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            var error = new ErrorDetails(
                    LocalDateTime.now(),
                    "Token expired",
                    ex.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), error);
            return;
        } catch (JwtException | AuthenticationException ex) {
            var error = new ErrorDetails(
                    LocalDateTime.now(),
                    "Invalid token. Recheck your JWT token",
                    ex.getMessage()
            );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), error);
            return;
        }
    }
}
