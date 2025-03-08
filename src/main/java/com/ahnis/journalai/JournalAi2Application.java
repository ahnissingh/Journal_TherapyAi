package com.ahnis.journalai;

import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.TimeZone;


/**
 * todo 1) Pagination and streak based system
 * todo 2 ) When finalised properties files add the packages here for better
 * todo 4 ) Consider defining all exception messages in yaml file for uniformity
 * todo 5) i When finalised project add @ConfigurationPropertiesScan classes or packages
 *        ii Use Mapper Scan for all map struct interfaces
 */
@SpringBootApplication(exclude = MilvusVectorStoreAutoConfiguration.class)
@EnableMongoAuditing
@ConfigurationPropertiesScan
@EnableScheduling

public class JournalAi2Application {
    //todo reports crud api
    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);

    }
}



