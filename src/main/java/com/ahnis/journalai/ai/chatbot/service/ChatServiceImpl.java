package com.ahnis.journalai.ai.chatbot.service;

import com.ahnis.journalai.ai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.user.entity.Preferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
    private static final String CUSTOM_USER_TEXT_ADVISE = """
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

    public ChatServiceImpl(ChatClient.Builder chatClient, ChatMemory chatMemory, MongoDBAtlasVectorStore vectorStore) {
        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
                        SearchRequest.builder()
                                .topK(3)
                                .build(), CUSTOM_USER_TEXT_ADVISE

                ))
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
                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "userId == '" + userId + "'")
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 30))
                .call()
                .content();

        return new ChatResponse(conversationId, response);
    }

    public Flux<String> chatFlux(ChatStreamRequest chatRequest, String chatId, Preferences userPreferences, String userId) {
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

                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "userId == '" + userId + "'")
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationFinalId)
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 30))
                .stream()
                .content();
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
