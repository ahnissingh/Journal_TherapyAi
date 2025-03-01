package com.ahnis.journalai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableMongoAuditing
@ConfigurationPropertiesScan
/**
 *
 * todo 1 ) Use mapping libraries instead of custom mappers
 * todo 2 ) When finalised properties files add the packages here for better
 * todo 3 ) Consider defining a token batching strategy for the project (Most required in our use case as journals need batching)
 * todo 4 ) Consider defining all exception messages in yaml file for uniformity
 *
 * https://docs.spring.io/spring-ai/reference/api/vectordbs.html
 */
public class JournalAi2Application {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);
    }
}




