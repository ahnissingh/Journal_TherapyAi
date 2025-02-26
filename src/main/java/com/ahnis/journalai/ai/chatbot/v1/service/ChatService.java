package com.ahnis.journalai.ai.chatbot.v1.service;

import com.ahnis.journalai.ai.chatbot.v1.controller.ChatBotController;
import com.ahnis.journalai.ai.chatbot.v1.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.v1.dto.ChatResponse;
import com.ahnis.journalai.entity.Preferences;
import reactor.core.publisher.Flux;

public interface ChatService {
    ChatResponse chatSync(Preferences userPreferences, ChatRequest request, String userId);

    Flux<String> chatFlux(ChatBotController.ChatRequest2 chatRequest, String chatId, Preferences preferences, String id);
}
