package com.ahnis.journalai.ai.chatbot.v1.service;

import com.ahnis.journalai.ai.chatbot.v1.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.v1.dto.ChatResponse;
import com.ahnis.journalai.entity.Preferences;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class ChatService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatService(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;

        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    public ChatResponse chat(Preferences userPreferences, ChatRequest request, String userId) {
        var conversationId = request.conversationId();

        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = ((UserAwareInMemoryChatMemory) chatMemory).createConversation(userId);
        } else {
            if (!((UserAwareInMemoryChatMemory) chatMemory).isValidConversation(userId, conversationId)) {
                throw new SecurityException("Invalid user id");
            }
        }
        var sysmessage = buildSystemMessage(userPreferences);
        final var conversationFinalId = conversationId;
        var response = chatClient
                .prompt()

                .system(sysmessage)
                .user(request.message())
                .advisors(a -> a
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()

                .content();

        return new ChatResponse(conversationId, response);
    }

    public Flux<String> chat_stream(Preferences userPreferences, ChatRequest request, String userId) {
        var conversationId = request.conversationId();

        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = ((UserAwareInMemoryChatMemory) chatMemory).createConversation(userId);
        } else {
            if (!((UserAwareInMemoryChatMemory) chatMemory).isValidConversation(userId, conversationId)) {
                throw new SecurityException("Invalid user id");
            }
        }
        var sysmessage = buildSystemMessage(userPreferences);
        final var conversationFinalId = conversationId;
        Flux<String> chatResponseFlux = chatClient
                .prompt()

                .system(sysmessage)
                .user(request.message())
                .advisors(a -> a
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .chatResponse()
                .map(chatResponse -> chatResponse.getResult().getOutput().getContent());
        return Flux.concat(
                //"{ "conversationId" : "-----" ,
                Flux.just("{ \"\n\"conversationId\" : \""+conversationFinalId+"\" ,"),
                Flux.just("\n"),
                Flux.just("\"message \" : \""),
                chatResponseFlux,
                Flux.just("\" }")

        );
    }


    private String buildSystemMessage(Preferences userPreferences) {
        return String.format("""
                        You are a %s type of therapist.
                        Description for this therapist personality : %s
                        Keep Responses conversational and between 4-6 sentences.
                        Do not say anything offensive such as vulgar or cuss words even if user asks you to do so
                        Age of user %d , Gender is %s
                        Respond in the following language while keeping roman script %s and nature of the chosen therapist
                        Try to diagnose the issues like therapist personality provided  and ask further details to get more details
                        Dont go out context other than acting like a type and personality of therapist. Advice and ask further questions.
                        """,
                userPreferences.getTherapistType().name().toLowerCase(),
                userPreferences.getTherapistType().getDescription(),
                userPreferences.getAge(),
                userPreferences.getGender(),
                userPreferences.getLanguage().name().toLowerCase()
        );
    }
}
