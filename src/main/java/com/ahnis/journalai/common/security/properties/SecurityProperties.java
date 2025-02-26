package com.ahnis.journalai.common.security.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
//todo refactor to records
@Component
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {
    @NotBlank(message = "Allowed origins must not be empty")
    private List<String> allowedOrigins;
}
