package com.ahnis.journalai.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    @NotBlank(message = "Jwt Secret must not blank")
    private String secret;
    @Positive(message = "Expiration of jwt must be positive")
    private Long expiration;
}
