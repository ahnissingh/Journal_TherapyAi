package com.ahnis.journalai.ai.chatbot.service;

import com.ahnis.journalai.ai.chatbot.payload.ChatRequest;
import com.ahnis.journalai.entity.User;
import reactor.core.publisher.Flux;

public interface ChatService {
    Flux<String> chatStream(User user, ChatRequest request);

}
