package com.ahnis.journalai.chatbot.service;

import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Primary
@Slf4j
//todo this approach vs questionAnswer approach (ask on linkedin)
//todo research if a whatsapp chatbot can be made
//todo note that if 2 advisors are used it takes more time in case of messagechat and vector store it took 15 seconds and in case of messagechat and questionanswer it took roughly less than that
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;

    @Value("classpath:/templates/chatbot/system-prompt.st")
    private Resource systemMessageResource;
    @Value("classpath:templates/chatbot/chatbot-prompt-template2.st")
    private Resource userChatbotPromptTemplateResource;
    private static final String CUSTOM_USER_TEXT_ADVISE =
            """
                    Context information is below, surrounded by ---------------------

                    ---------------------
                    {question_answer_context}
                    ---------------------
                    Given the context of the user's journal entries above, provide a thoughtful and empathetic response.
                    Focus on the emotions, experiences, and insights shared in the journals.
                    If the user's question is related to their mood or experiences, analyze the context and provide a summary or advice based on the entries.
                    If the question cannot be answered using the context, politely inform the user that you don't have enough information to respond.

                    Your response should be:
                    - Personalized: Address the user directly and reference specific details from their journals or their stored preferences
                    - Empathetic: Acknowledge the user's emotions and experiences.
                    - Actionable: Provide suggestions or insights that the user can act upon.
                    - Concise: Keep the response clear and to the point.

                    Do not include any explanations or notes about the process—only provide the response.
                    """;

    public ChatServiceImpl(ChatClient.Builder chatClient, ChatMemory chatMemory, VectorStore vectorStore) {
        this.chatClient = chatClient
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
                        SearchRequest.builder()
                                .topK(4)
                                .build(), CUSTOM_USER_TEXT_ADVISE

                ))
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();


    }

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
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 30))
                .call()
                .content();

        return new ChatResponse(conversationId, response);
    }


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
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 30))
                .stream()
                .content();
    }

    private Prompt createUserChatBotPrompt(User user, String chatRequestMessage) {
        var userChatbotPromptTemplate = new PromptTemplate(userChatbotPromptTemplateResource);
        var userPreferences = user.getPreferences();
        //More than 10 variables so using HashMap instead of Map.of
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
        //Each user is forced to have a next Report on because when they register this field is registered
        userPreferencesMap.put("nextReportOn", user.getNextReportOn().atZone(ZoneId.of(user.getTimezone())).toString());
        userPreferencesMap.put("userMessage", chatRequestMessage);
        return userChatbotPromptTemplate.create(userPreferencesMap);
    }

    private String createConversation(String userId) {
        // Generate a new conversationId in the format "userId:conversationId"
        //noinspection StringTemplateMigration
        return userId + ":" + UUID.randomUUID();
    }

    private boolean isValidConversation(String userId, String conversationId) {
        // Validate that the conversationId starts with the userId
        return conversationId.startsWith(userId + ":");
    }


}
