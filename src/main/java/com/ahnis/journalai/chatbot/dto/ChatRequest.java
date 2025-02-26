package com.ahnis.journalai.chatbot.dto;

public record ChatRequest(
        String conversationId, // Optional: If not provided, a new conversation is created
        String message         // The user's message
) {

}
