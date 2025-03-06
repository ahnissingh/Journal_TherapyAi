package com.ahnis.journalai.ai.chatbot.chatmemory.config;



import com.ahnis.journalai.ai.chatbot.chatmemory.custom.MongoDbChatMemory;
import com.ahnis.journalai.ai.chatbot.chatmemory.custom.MongoDbChatMemoryConfig;

import com.mongodb.client.MongoClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ChatMemoryConfig {


    @Bean
    public ChatMemory chatMemory(MongoClient mongoClient) {
        return MongoDbChatMemory.create(MongoDbChatMemoryConfig.builder()
                .withMongoClient(mongoClient)
                .withCollectionName("chat_memory")
                .withDatabaseName("journal_ai")
                .withTimeToLive(Duration.ofDays(90))
                .build()
        );
    }

}
