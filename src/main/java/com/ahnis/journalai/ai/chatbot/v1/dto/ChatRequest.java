package com.ahnis.journalai.ai.chatbot.v1.dto;

public record ChatRequest(
        String conversationId, // Optional: If not provided, a new conversation is created
        String message         // The user's message
) {

}
