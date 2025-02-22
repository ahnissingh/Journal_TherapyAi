package com.ahnis.journalai;

import org.springframework.ai.autoconfigure.vectorstore.cassandra.CassandraVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.cassandra.CassandraVectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication(exclude = {CassandraVectorStoreAutoConfiguration.class})
@EnableMongoAuditing

@ConfigurationPropertiesScan("com.ahnis.journalai.config.properties")
//todo use mapping libraries instead of custom mappers
public class JournalAi2Application {
    public static void main(String[] args) {
        SpringApplication.run(JournalAi2Application.class, args);
    }

}
