package com.ahnis.journalai.chatbot.dto;
public record ChatResponse(
        String conversationId, // The conversation ID for this chat session
        String response        // The chatbot's response
) {}
