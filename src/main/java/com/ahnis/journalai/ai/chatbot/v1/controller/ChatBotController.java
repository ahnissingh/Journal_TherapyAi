package com.ahnis.journalai.ai.chatbot.v1.controller;

import com.ahnis.journalai.ai.chatbot.v1.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.v1.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.v1.service.ChatService;
import com.ahnis.journalai.entity.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")

public class ChatBotController {
    private final ChatService chatService;

    public ChatBotController(ChatService chatService) {
        this.chatService = chatService;

    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chat(
            @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal User user
    ) {
        return chatService.chat(user.getPreferences(), chatRequest, user.getId());
    }

    @PostMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> chatStreamResponseFlux(
            @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal User user
    ) {
        return chatService.chat_stream(user.getPreferences(), chatRequest, user.getId());
    }


}
