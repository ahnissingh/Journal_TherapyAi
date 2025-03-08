package com.ahnis.journalai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreAutoConfiguration;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.annotation.Native;
import java.util.TimeZone;

@SpringBootApplication(exclude = MilvusVectorStoreAutoConfiguration.class)
@EnableMongoAuditing
@ConfigurationPropertiesScan
@EnableScheduling

/**
 * todo 2 ) When finalised properties files add the packages here for better
 * todo 3 ) Consider defining a token batching strategy for the project (Most required in our use case as journals need batching)
 * todo 4 ) Consider defining all exception messages in yaml file for uniformity
 *
 * https://docs.spring.io/spring-ai/reference/api/vectordbs.html
 */
@RegisterReflectionForBinding()
public class JournalAi2Application {
    //todo reports crud api
    public static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(JournalAi2Application.class, args);

    }


}



