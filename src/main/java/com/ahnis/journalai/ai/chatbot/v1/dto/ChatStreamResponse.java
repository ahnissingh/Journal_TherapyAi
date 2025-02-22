package com.ahnis.journalai.ai.chatbot.v1.dto;

public record ChatStreamResponse(
        String conversationId, // The conversation ID for this chat session
        String message         // The streamed message (can be null for the conversationId)
) {}
