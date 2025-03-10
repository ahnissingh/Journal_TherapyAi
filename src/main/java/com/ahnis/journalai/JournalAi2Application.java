package com.ahnis.journalai;

import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.notification.service.SendGridEmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;


/**
 * todo 1) Pagination and streak based system
 * todo 2 ) When finalised properties files add the packages here for better
 * todo 4 ) Consider defining all exception messages in yaml file for uniformity
 * todo 5) i When finalised project add @ConfigurationPropertiesScan classes or packages
 *        ii Use Mapper Scan for all map struct interfaces
 */
@Slf4j
@SpringBootApplication(exclude = MilvusVectorStoreAutoConfiguration.class)
@EnableMongoAuditing
@ConfigurationPropertiesScan
@EnableScheduling
public class JournalAi2Application {
    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        //todo dont remove this is actually being used
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);


    }

}



