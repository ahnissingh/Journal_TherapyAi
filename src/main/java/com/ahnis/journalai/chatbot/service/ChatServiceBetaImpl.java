package com.ahnis.journalai.chatbot.service;

import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.chatbot.entity.ChatSession;
import com.ahnis.journalai.chatbot.exception.InvalidSessionException;
import com.ahnis.journalai.chatbot.tools.SuicidePreventionTool;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceBetaImpl implements ChatService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatSessionService chatSessionService;
    @Value("classpath:/templates/chatbot/system-template.st")
    private Resource systemMessageResource;
    @Value("classpath:templates/chatbot/chatbot-template.st")
    private Resource userChatbotPromptTemplateResource;


    public ChatResponse chatSync(User user, ChatRequest chatRequest) {
        String userId = user.getId();
        String sessionId = chatRequest.conversationId();

        if (sessionId == null || sessionId.isEmpty()) {
            ChatSession newSession = chatSessionService.createNewSession(userId);
            sessionId = newSession.getId();
        } else if (!chatSessionService.isValidSession(sessionId, userId)) {
            throw new InvalidSessionException("Invalid session ID");
        }

        // Save user message
        chatSessionService.addUserMessage(sessionId, userId, chatRequest.message());

        Prompt userChatbotPrompt = createUserChatBotPrompt(user, chatRequest.message());

        String response = chatClient.prompt(userChatbotPrompt)
                .system(systemMessageResource)
                .toolContext(createToolContext(userId, user.getUsername()))
                .advisors(advisorSpecification(userId, chatRequest.message(), sessionId))
                .call()
                .content();

        // Save assistant response
        chatSessionService.addAssistantMessage(sessionId, userId, response);

        return new ChatResponse(sessionId, response);
    }

    //    public Flux<String> chatFlux(ChatStreamRequest chatRequest, String sessionId, User user) {
//        String userId = user.getId();
//
//        if (sessionId == null) {
//            ChatSession newSession = chatSessionService.createNewSession(userId);
//            sessionId = newSession.getId();
//        } else if (!chatSessionService.isValidSession(sessionId, userId)) {
//            return Flux.error(new SecurityException("Invalid session ID"));
//        }
//
//        // Save user message
//        chatSessionService.addUserMessage(sessionId, userId, chatRequest.message());
//
//        Prompt userChatbotPrompt = createUserChatBotPrompt(user, chatRequest.message());
//
//        String finalSessionId = sessionId;
//        return chatClient.prompt(userChatbotPrompt)
//                .system(systemMessageResource)
//                .toolContext(createToolContext(userId, user.getUsername()))
//                .advisors(advisorSpecification(userId, chatRequest.message(), sessionId))
//                .stream()
//                .content()
//                .onBackpressureBuffer()
//                .doOnComplete(() -> {
//                    // In a real implementation, you'd accumulate the streamed response
//                    // and save it when complete. This is simplified.
//                    chatSessionService.addAssistantMessage(finalSessionId, userId,
//                            "Streamed conversation (full message not captured)");
//                });
//    }
    public Flux<String> chatFlux(ChatStreamRequest chatRequest, String sessionId, User user) {
        String userId = user.getId();

        if (sessionId == null) {
            ChatSession newSession = chatSessionService.createNewSession(userId);
            sessionId = newSession.getId();
        } else if (!chatSessionService.isValidSession(sessionId, userId)) {
            return Flux.error(new InvalidSessionException("Invalid session ID"));
        }

        // Save user message
        chatSessionService.addUserMessage(sessionId, userId, chatRequest.message());

        Prompt userChatbotPrompt = createUserChatBotPrompt(user, chatRequest.message());

        // Just collect the whole response as a string
        StringBuilder fullResponse = new StringBuilder();
        //Final reference required for lambda exp
        String finalSessionId = sessionId;
        return chatClient.prompt(userChatbotPrompt)
                .system(systemMessageResource)
                .toolContext(createToolContext(userId, user.getUsername()))
                .advisors(advisorSpecification(userId, chatRequest.message(), sessionId))
                .stream()
                .content()
                .map(chunk -> {
                    fullResponse.append(chunk); // Collect chunks
                    return chunk; // Still stream each chunk to client
                })
                .doOnComplete(() -> {
                    // Save complete response when done
                    chatSessionService.addAssistantMessage(finalSessionId, userId, fullResponse.toString());
                });
    }

    /**
     * Configures the advisors for the chat client, including the {@link QuestionAnswerAdvisor} and {@link MessageChatMemoryAdvisor}.
     * This method sets up the necessary parameters for the advisors to provide personalized and context-aware responses.
     *
     * <p>
     * The {@link QuestionAnswerAdvisor} is configured with a {@link SearchRequest} that filters journals by the user's ID,
     * limits the results to the top K relevant journals, and uses the user's message as the query for semantic search.
     * </p>
     *
     * <p>
     * The {@link MessageChatMemoryAdvisor} is configured with the conversation ID and retrieve size to manage short-term
     * conversation memory for context-aware interactions.
     * </p>
     *
     * @param userId         The unique ID of the user, used to filter journals by the user's ID.
     * @param message        The user's message, used as the query for semantic search in the {@link QuestionAnswerAdvisor}.
     * @param conversationId The unique ID of the conversation, used to manage short-term memory in the {@link MessageChatMemoryAdvisor}.
     * @return A {@link Consumer} that configures the {@link ChatClient.AdvisorSpec} with the necessary advisors and parameters.
     */
    private Consumer<ChatClient.AdvisorSpec> advisorSpecification(String userId, String message, String conversationId) {
        // Format dates to match what works in similaritySearch
        Instant now = Instant.now();
        Instant startDate = now.minus(0, ChronoUnit.DAYS);
        log.info("Start Date and end date {} {}", startDate, now);

        return advisorSpec -> advisorSpec
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder()
                        .filterExpression("userId == '" + userId + "'")
                        .topK(3)
                        .build()))
                .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5);
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

    private static Map<String, Object> createToolContext(String userId, String username) {
        return Map.of(
                "userId", userId,
                "username", username
        );
    }
}
