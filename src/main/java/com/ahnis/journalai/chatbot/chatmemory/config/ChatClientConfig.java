package com.ahnis.journalai.chatbot.chatmemory.config;

import com.ahnis.journalai.chatbot.tools.SuicidePreventionTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(@Qualifier("openAiChatModel") ChatModel chatModel, ChatMemory chatMemory, SuicidePreventionTool chatbotTools) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(
                        new MessageChatMemoryAdvisor(chatMemory)
                ))
                .defaultTools(chatbotTools)

                .build();
    }
}
