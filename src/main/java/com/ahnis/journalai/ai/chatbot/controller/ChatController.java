package com.ahnis.journalai.ai.chatbot.controller;

import com.ahnis.journalai.ai.chatbot.payload.ChatRequest;
import com.ahnis.journalai.ai.chatbot.service.ChatService;
import com.ahnis.journalai.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController()
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/stream")
    public Flux<String> chatStream(@RequestBody ChatRequest chatRequest, @AuthenticationPrincipal User user) {
        return chatService.chatStream(user,chatRequest);
    }

}
