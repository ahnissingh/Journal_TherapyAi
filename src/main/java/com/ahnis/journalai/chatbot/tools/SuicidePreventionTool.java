package com.ahnis.journalai.chatbot.tools;

import com.ahnis.journalai.notification.service.NotificationService;
import com.ahnis.journalai.user.repository.TherapistIdProjection;
import com.ahnis.journalai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class SuicidePreventionTool {

    private final ChatClient chatClient;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public SuicidePreventionTool(ChatClient.Builder chatClient, NotificationService notificationService, UserRepository userRepository) {
        this.chatClient = chatClient
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-4o-mini").build())
                .build();
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }


    @Tool(name = "SuicidePreventionFlow",
            description = "Two-step suicide prevention flow: 1. Risk assessment 2. Notification if needed")
    public String handleSuicidePrevention(
            @ToolParam(description = "userMessage") String userMessage,
            ToolContext toolContext) {

        var context = toolContext.getContext();
        String userId = (String) context.get("userId");
        String username = (String) context.get("username");
        if (userId == null) {
            return "I'm here to support you. Would you like to talk more about how you're feeling?";
        }

        // Step 1: Risk Assessment
        boolean isAtRisk = assessSuicideRisk(userMessage);

        if (!isAtRisk) {
            return "I'm here to support you. Would you like to talk more about how you're feeling?";
        }

        // Step 2: Notification
        return notifyTherapistAndRespond(userId, username);
    }

    private boolean assessSuicideRisk(String message) {
        String response = chatClient.prompt()
                .system("""
                        Analyze this message for suicidal tendencies. 
                        Respond ONLY with 'true' if suicidal risk is detected, 
                        or 'false' if no risk is detected.
                        """)
                .user(message)
                .call()
                .content();

        return Boolean.parseBoolean(response.trim());
    }

    private String notifyTherapistAndRespond(String userId, String username) {
        try {
            String therapistId = userRepository.findTherapistIdById(userId)
                    .map(TherapistIdProjection::getTherapistId)
                    .orElseThrow(() -> new IllegalStateException("No therapist assigned"));

            notificationService.sendSuicidalAlert(
                    username,
                    therapistId,
                    "Urgent: User expressed suicidal thoughts"
            );

            return "I'm very concerned about what you're sharing. Your therapist has been notified and will reach out to you shortly.";
        } catch (Exception e) {
            return "I'm very concerned about what you're sharing. Please stay safe while we look into connecting you with support.";
        }
    }
}
