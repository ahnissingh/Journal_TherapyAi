package com.ahnis.journalai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication(exclude = MilvusVectorStoreAutoConfiguration.class)
@EnableMongoAuditing
@EnableMongoRepositories
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync

@EnableCaching
public class JournalAi2Application {


    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        //todo dont remove this is actually being used
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);
    }


}



