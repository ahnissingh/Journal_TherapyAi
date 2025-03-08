package com.ahnis.journalai.chatbot.service;

import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.user.entity.Preferences;
import com.ahnis.journalai.user.entity.User;
import reactor.core.publisher.Flux;

public interface ChatService {
    ChatResponse chatSync(User user, ChatRequest request, String userId);

    Flux<String> chatFlux(ChatStreamRequest chatRequest, String chatId, User user, String id);

}
