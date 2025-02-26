package com.ahnis.journalai.ai.chatbot.service.impl;

import com.ahnis.journalai.ai.chatbot.controller.ChatBotController;
import com.ahnis.journalai.ai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.service.ChatService;
import com.ahnis.journalai.user.entity.Preferences;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@Service
@Primary
public class ChatServiceRagStoreImpl implements ChatService {
    private final ChatClient chatClient;

    @Value("classpath:/templates/chatbot/system-prompt.st")
    private Resource systemMessageResource;
    @Value("classpath:templates/chatbot/chatbot-prompt-template2.st")
    private Resource userChatbotPromptTemplateResource;

    public ChatServiceRagStoreImpl(ChatClient.Builder chatClient, ChatMemory chatMemory, VectorStore vectorStore) {
        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                // todo this approach vs questionAnswer approach (ask on linkedin)
                //todo research if a whatsapp chatbot can be made
                //todo note that if 2 advisors are used it takes more time in case of messagechat and vector store it took 15 seconds and in case of messagechat and questionanswer it took roughly less than that
                .build();
    }


    public ChatResponse chatSync(Preferences userPreferences, ChatRequest chatRequest, String userId) {
        var conversationId = chatRequest.conversationId();

        // If no conversationId is provided, create a new one
        // Validate that the conversationId belongs to the user
        if (conversationId == null || conversationId.isEmpty()) conversationId = createConversation(userId);
        else if (!isValidConversation(userId, conversationId)) throw new SecurityException("Invalid user id");

        var userChatbotPromptTemplate = new PromptTemplate(userChatbotPromptTemplateResource);

        Map<String, Object> userPreferencesMap = Map.of(
                "supportStyle", userPreferences.getSupportStyle().toString(),
                "supportStyleDescription", userPreferences.getSupportStyle().getDescription(),
                "language", userPreferences.getLanguage(),
                "userAge", userPreferences.getAge(),
                "userGender", userPreferences.getGender(),
                "userMessage", chatRequest.message()
        );

        Prompt userChatbotPrompt = userChatbotPromptTemplate.create(userPreferencesMap);

        final var conversationFinalId = conversationId;
        var response = chatClient
                .prompt(userChatbotPrompt)
                .system(systemMessageResource)

                .advisors(a -> a
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content();

        return new ChatResponse(conversationId, response);
    }

    public Flux<String> chatFlux(ChatBotController.ChatRequest2 chatRequest, String chatId, Preferences userPreferences, String userId) {
        if (chatId == null) chatId = createConversation(userId);
        else if (!isValidConversation(userId, chatId)) throw new SecurityException("Invalid chat id for user ");
        var userChatbotPromptTemplate = new PromptTemplate(userChatbotPromptTemplateResource);

        Map<String, Object> userPreferencesMap = Map.of(
                "supportStyle", userPreferences.getSupportStyle().toString(),
                "supportStyleDescription", userPreferences.getSupportStyle().getDescription(),
                "language", userPreferences.getLanguage(),
                "userAge", userPreferences.getAge(),
                "userGender", userPreferences.getGender(),
                "userMessage", chatRequest.message()
        );

        Prompt userChatbotPrompt = userChatbotPromptTemplate.create(userPreferencesMap);

        final var conversationFinalId = chatId;
        return chatClient
                .prompt(userChatbotPrompt)
                .system(systemMessageResource)
                .advisors(a -> a
                        .param(VectorStoreChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }

    private String createConversation(String userId) {
        // Generate a new conversationId in the format "userId:conversationId"
        return userId + ":" + UUID.randomUUID();
    }

    private boolean isValidConversation(String userId, String conversationId) {
        // Validate that the conversationId starts with the userId
        return conversationId.startsWith(userId + ":");
    }


}
