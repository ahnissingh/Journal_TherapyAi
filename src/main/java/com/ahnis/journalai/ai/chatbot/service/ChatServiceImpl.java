package com.ahnis.journalai.ai.chatbot.service;

import com.ahnis.journalai.ai.chatbot.payload.ChatRequest;
import com.ahnis.journalai.entity.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatServiceImpl(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @Override
    public Flux<String> chatStream(User user, ChatRequest request) {
        String systemMessage = String.format("""
                        You are a %s type of therapist.
                        %s
                        Keep Responses conversational and between 4-6 sentences.
                        Do not say anything offensive such as vulgar or cuss words even if user asks you to do so
                        Respond in the following language while keeping roman script %s
                        """,
                user.getPreferences().getTherapistType().name().toLowerCase(),
                user.getPreferences().getTherapistType().getDescription(),
                user.getPreferences().getLanguage().name().toLowerCase()
        );
        return chatClient
                .prompt()
                .system(systemMessage)
                .user(request.message())
                .stream().content();

    }


}
