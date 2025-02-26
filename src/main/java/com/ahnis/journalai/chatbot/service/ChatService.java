package com.ahnis.journalai.chatbot.service;

import com.ahnis.journalai.chatbot.controller.ChatBotController;
import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.user.entity.Preferences;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {
    ChatResponse chatSync(Preferences userPreferences, ChatRequest request, String userId);

    Flux<String> chatFlux(ChatBotController.ChatRequest2 chatRequest, String chatId, Preferences preferences, String id);

    Flux<String> chatFlux2(ChatBotController.ChatRequest2 chatRequest, String chatId, Preferences preferences, String id);
}
