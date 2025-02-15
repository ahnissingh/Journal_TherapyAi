package com.ahnis.journalai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class JournalAi2Application {

    public static void main(String[] args) {
        SpringApplication.run(JournalAi2Application.class, args);
    }

}
