package com.ahnis.journalai.notification.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SendGridConfig {
    private final SendGridProperties sendGridProperties;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridProperties.apiKey());
    }
}
