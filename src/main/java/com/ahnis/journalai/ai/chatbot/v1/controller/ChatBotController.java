package com.ahnis.journalai.ai.chatbot.v1.controller;

import com.ahnis.journalai.ai.chatbot.v1.dto.ChatRequest;
import com.ahnis.journalai.ai.chatbot.v1.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.v1.service.ChatService;
import com.ahnis.journalai.entity.User;
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

    @PostMapping()
    public ChatResponse chat(
            @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal User user
    ) {
        return chatService.chatSync(user.getPreferences(), chatRequest, user.getId());
    }
    @PostMapping("/c/{chatId}")
    public Flux<String> chatStream(@PathVariable(required = false) String chatId, @RequestBody ChatRequest2 chatRequest, @AuthenticationPrincipal User user) {
        return chatService.chatFlux(chatRequest, chatId, user.getPreferences(), user.getId());
    }

    public record ChatRequest2(String message) {
    }
}


