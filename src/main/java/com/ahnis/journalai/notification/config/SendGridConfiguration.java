package com.ahnis.journalai.notification.config;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SendGridConfiguration {

    @Bean
    @Primary
    public SendGrid sendGrid(SendGridProperties sendGridProperties) {
        return new SendGrid(sendGridProperties.apiKey());
    }
}
