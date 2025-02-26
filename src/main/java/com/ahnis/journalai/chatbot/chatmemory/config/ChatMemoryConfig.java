package com.ahnis.journalai.chatbot.chatmemory.config;

import com.ahnis.journalai.chatbot.chatmemory.custom.MongoDbChatMemory;
import com.ahnis.journalai.chatbot.chatmemory.custom.MongoDbChatMemoryConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ChatMemoryConfig {


    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.authentication-database}")
    private String authDatabase;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Bean
    public MongoClient mongoClient() {
        String connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s",
                username, password, "localhost", mongoPort, database, authDatabase);

        // Create and return the MongoClient
        return MongoClients.create(connectionString);
    }

    @Bean
    public ChatMemory chatMemory(MongoClient mongoClient) {
        return MongoDbChatMemory.create(MongoDbChatMemoryConfig.builder()
                .withMongoClient(mongoClient)
                .withCollectionName(database)
                .withCollectionName("chat_memory")
                .withTimeToLive(Duration.ofDays(90))
                .build());
    }
}
