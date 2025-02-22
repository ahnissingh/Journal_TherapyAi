package com.ahnis.journalai.ai.chatbot.v1.config;

import com.ahnis.journalai.ai.chatbot.v1.service.UserAwareInMemoryChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfiguration {
    @Bean
    public ChatMemory chatMemory() {
        return new UserAwareInMemoryChatMemory();
    }
}
