package com.ahnis.journalai.user.bootstrap;

import com.ahnis.journalai.user.dto.request.UserRegistrationRequest;
import com.ahnis.journalai.user.service.AdminService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "admin.bootstrap", name = "enabled", havingValue = "true")
@Slf4j
public class InitializeUsersFromJson implements CommandLineRunner {

    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    private final ResourceLoader resourceLoader;  // Add this for dynamic resource loading

    @Value("${admin.bootstrap.resource}")
    private String userJsonResourcePath;  // Injected from application.yml

    public InitializeUsersFromJson(AdminService adminService, ObjectMapper objectMapper, @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.adminService = adminService;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) {
        log.info("Starting InitializeUsersFromJson CommandLineRunner...");

        try {
            List<UserRegistrationRequest> users = loadUsersFromJson();
            log.info("Loaded {} users from '{}'.", users.size(), userJsonResourcePath);

            if (users.isEmpty()) {
                log.warn("No users found in JSON file. Skipping registration.");
                return;
            }

            adminService.registerMultipleUsers(users);
            log.info("Successfully registered {} users.", users.size());
        } catch (Exception e) {
            log.error("Failed to initialize users from JSON file '{}'.", userJsonResourcePath, e);
        }
    }

    private List<UserRegistrationRequest> loadUsersFromJson() throws IOException {
        Resource resource = resourceLoader.getResource(userJsonResourcePath);  // Load dynamically
        if (!resource.exists()) {
            throw new IOException("JSON file not found at: " + userJsonResourcePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        }
    }
}
