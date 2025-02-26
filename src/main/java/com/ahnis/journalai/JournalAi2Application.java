package com.ahnis.journalai;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableMongoAuditing


@ConfigurationPropertiesScan("com.ahnis.journalai.config.properties")
//todo use mapping libraries instead of custom mappers
public class JournalAi2Application {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);
    }

}
