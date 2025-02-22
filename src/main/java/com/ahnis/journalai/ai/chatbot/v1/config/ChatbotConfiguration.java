package com.ahnis.journalai.ai.chatbot.v1.config;

import com.ahnis.journalai.ai.chatbot.v1.service.UserAwareInMemoryChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory;
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemoryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

//@Configuration

public class ChatbotConfiguration {

//    public ChatMemory chatMemory() {
////
////        return new UserAwareInMemoryChatMemory();
//        return CassandraChatMemory.create(
//                CassandraChatMemoryConfig.builder()
//                        .withTimeToLive(Duration.ofDays(90))
//                        .build()
//        );
//    }
}
