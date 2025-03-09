package com.ahnis.journalai.chatbot.service;

import com.ahnis.journalai.chatbot.config.ChatAdviceConstants;
import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.user.entity.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The primary implementation of the {@link ChatService} interface.
 * This service handles synchronous and asynchronous (streaming) chat interactions
 * with the user, leveraging various advisors to provide personalized and context-aware responses.
 * <p>
 * The service uses the following advisors:
 * <ul>
 *   <li>{@link QuestionAnswerAdvisor}: Provides responses based on the user's journal entries and preferences.</li>
 *   <li>{@link MessageChatMemoryAdvisor}: Manages short-term conversation memory for context-aware interactions.</li>
 *   <li>{@link VectorStoreChatMemoryAdvisor}: Utilizes long-term conversation memory stored in a vector store for accurate and personalized responses.</li>
 * </ul>
 * </p>
 * <p>
 * The service also uses predefined prompt templates for system messages and user interactions,
 * which are loaded from external resources.
 * </p>
 *
 * <p>
 * Written by: Ahnis Singh
 * </p>
 */
@Service
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;

    @Value("classpath:/templates/chatbot/system-template.st")
    private Resource systemMessageResource;
    @Value("classpath:templates/chatbot/chatbot-template.st")
    private Resource userChatbotPromptTemplateResource;

    /**
     * Constructs a new instance of {@link ChatServiceImpl}.
     *
     * @param chatClient  The {@link ChatClient.Builder} used to build the chat client.
     * @param chatMemory  The {@link ChatMemory} used for short-term conversation memory.
     * @param vectorStore The {@link VectorStore} used for long-term conversation memory.
     */

    public ChatServiceImpl(ChatClient.Builder chatClient, ChatMemory chatMemory, VectorStore vectorStore) {
//        this.chatClient = chatClient
//                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
//                        SearchRequest.builder()
//                                .topK(2)
//                                .build(), ChatAdviceConstants.QUESTION_ANSWER_ADVISOR_PROMPT
//                ))
//                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
//                .defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(vectorStore).systemTextAdvise(ChatAdviceConstants.VECTOR_CHAT_ADVISOR_PROMPT).build())
//                .build();
        this.chatClient = chatClient.defaultAdvisors(List.of(
                new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(2).build()),
//                VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
                new MessageChatMemoryAdvisor(chatMemory)
        )).build();
    }

    /**
     * Handles synchronous chat interactions with the user.
     *
     * @param user        The {@link User} initiating the chat.
     * @param chatRequest The {@link ChatRequest} containing the user's message and conversation ID.
     * @param userId      The unique ID of the user.
     * @return A {@link ChatResponse} containing the chatbot's response and the conversation ID.
     * @throws SecurityException If the conversation ID is invalid for the given user.
     */
    public ChatResponse chatSync(User user, ChatRequest chatRequest, String userId) {
        var conversationId = chatRequest.conversationId();
        if (conversationId == null || conversationId.isEmpty()) conversationId = createConversation(userId);
        else if (!isValidConversation(userId, conversationId)) throw new SecurityException("Invalid user id");

        Prompt userChatbotPrompt = createUserChatBotPrompt(user, chatRequest.message());

        final var conversationFinalId = conversationId;
        var response = chatClient
                .prompt(userChatbotPrompt)
                .system(systemMessageResource)
                .advisors(a -> a
                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "userId == '" + userId + "'")
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
//                        .param(VectorStoreChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
//                        .param(VectorStoreChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 4)

                )
                .call()
                .content();

        return new ChatResponse(conversationId, response);
    }

    /**
     * Handles asynchronous (streaming) chat interactions with the user.
     *
     * @param chatRequest The {@link ChatStreamRequest} containing the user's message and chat ID.
     * @param chatId      The unique ID of the chat session.
     * @param user        The {@link User} initiating the chat.
     * @param userId      The unique ID of the user.
     * @return A {@link Flux} of strings representing the chatbot's streaming response.
     * @throws SecurityException If the chat ID is invalid for the given user.
     */
    public Flux<String> chatFlux(ChatStreamRequest chatRequest, String chatId, User user, String userId) {
        if (chatId == null) chatId = createConversation(userId);
        else if (!isValidConversation(userId, chatId)) throw new SecurityException("Invalid chat id for user ");

        Prompt userChatbotPrompt = createUserChatBotPrompt(user, chatRequest.message());

        final var conversationFinalId = chatId;
        return chatClient
                .prompt(userChatbotPrompt)
                .system(systemMessageResource)
                .advisors(a -> a
                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "userId == '" + userId + "'")
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
//                        .param(VectorStoreChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
//                        .param(VectorStoreChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 4)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content()
                .onBackpressureBuffer();
    }

    /**
     * Creates a {@link Prompt} for the chatbot based on the user's preferences and message.
     *
     * @param user               The {@link User} initiating the chat.
     * @param chatRequestMessage The user's message.
     * @return A {@link Prompt} containing the user's preferences and message.
     */
    private Prompt createUserChatBotPrompt(User user, String chatRequestMessage) {
        var userChatbotPromptTemplate = new PromptTemplate(userChatbotPromptTemplateResource);
        var userPreferences = user.getPreferences();
        Map<String, Object> userPreferencesMap = new LinkedHashMap<>();
        userPreferencesMap.put("supportStyle", userPreferences.getSupportStyle().toString());
        userPreferencesMap.put("supportStyleDescription", userPreferences.getSupportStyle().getDescription());
        userPreferencesMap.put("username", user.getUsername());
        userPreferencesMap.put("language", userPreferences.getLanguage());
        userPreferencesMap.put("userAge", userPreferences.getAge());
        userPreferencesMap.put("userGender", userPreferences.getGender());
        userPreferencesMap.put("timeZone", user.getTimezone());
        userPreferencesMap.put("currentStreak", user.getCurrentStreak());
        userPreferencesMap.put("longestStreak", user.getLongestStreak());
        userPreferencesMap.put("lastJournalEntryDate", user.getLastJournalEntryDate() != null ? user.getLastJournalEntryDate().atZone(ZoneId.of(user.getTimezone())).toString() : "null");
        userPreferencesMap.put("lastReportAt", user.getLastReportAt() != null ? user.getLastReportAt() : "null");
        userPreferencesMap.put("nextReportOn", user.getNextReportOn().atZone(ZoneId.of(user.getTimezone())).toString());
        userPreferencesMap.put("userMessage", chatRequestMessage);
        return userChatbotPromptTemplate.create(userPreferencesMap);
    }

    /**
     * Generates a new conversation ID in the format "userId:conversationId".
     *
     * @param userId The unique ID of the user.
     * @return A new conversation ID.
     */
    private String createConversation(String userId) {
        return userId + ":" + UUID.randomUUID();
    }

    /**
     * Validates that the conversation ID belongs to the given user.
     *
     * @param userId         The unique ID of the user.
     * @param conversationId The conversation ID to validate.
     * @return {@code true} if the conversation ID is valid for the user, {@code false} otherwise.
     */
    private boolean isValidConversation(String userId, String conversationId) {
        return conversationId.startsWith(userId + ":");
    }
}

