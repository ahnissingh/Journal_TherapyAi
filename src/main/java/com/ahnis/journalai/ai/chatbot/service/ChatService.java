package com.ahnis.journalai.ai.chatbot.service;

import com.ahnis.journalai.ai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.user.entity.Preferences;
import reactor.core.publisher.Flux;

public interface ChatService {
    ChatResponse chatSync(Preferences userPreferences, ChatRequest request, String userId);

    Flux<String> chatFlux(ChatStreamRequest chatRequest, String chatId, Preferences preferences, String id);

}
