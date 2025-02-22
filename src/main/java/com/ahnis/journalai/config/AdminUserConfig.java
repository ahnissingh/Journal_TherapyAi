package com.ahnis.journalai.config;

import com.ahnis.journalai.config.properties.AdminUserProperties;
import com.ahnis.journalai.entity.Preferences;
import com.ahnis.journalai.entity.User;
import com.ahnis.journalai.enums.*;
import com.ahnis.journalai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminUserConfig {

    private final AdminUserProperties adminUserProperties;

    @Bean
    @ConditionalOnProperty(name = "admin.user.enabled", havingValue = "true")
    CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> userRepository.findByUsername(adminUserProperties.username())
                .ifPresentOrElse(
                        this::logUserAlreadyExists,
                        () -> buildAndSaveUser(userRepository, passwordEncoder)
                );
    }

    private void logUserAlreadyExists(User user) {
        log.error("User already exists: {}", user.getUsername());
    }

    private void buildAndSaveUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        var adminUser = User.builder()
                .username(adminUserProperties.username())
                .email(adminUserProperties.email())
                .password(passwordEncoder.encode(adminUserProperties.password()))
                .roles(Set.of(Role.ADMIN))
                .preferences(adminUserProperties.preferences())
                .build();
        userRepository.save(adminUser);
        log.info("Admin user created with username: {} and email: {}", adminUser.getUsername(), adminUser.getEmail());
    }
}
