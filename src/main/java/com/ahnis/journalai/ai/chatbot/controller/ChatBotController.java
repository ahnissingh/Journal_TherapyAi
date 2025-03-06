package com.ahnis.journalai.ai.chatbot.controller;

import com.ahnis.journalai.ai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.ai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.ai.chatbot.service.ChatService;
import com.ahnis.journalai.ai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatBotController {
    private final ChatService chatService;


    //Step1- Get a chat id
    @PostMapping()
    public ChatResponse chat(
            @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal User user
    ) {
        return chatService.chatSync(user.getPreferences(), chatRequest, user.getId());
    }

    //Step2- use the chat id and get streaming response
    @PostMapping(value = "/c/{chatId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chatStream(@PathVariable(required = false) String chatId, @RequestBody ChatStreamRequest chatRequest, @AuthenticationPrincipal User user) {
        return chatService.chatFlux(chatRequest, chatId, user.getPreferences(), user.getId());
    }


}


