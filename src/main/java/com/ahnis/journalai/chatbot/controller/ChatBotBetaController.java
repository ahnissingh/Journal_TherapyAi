package com.ahnis.journalai.chatbot.controller;


import com.ahnis.journalai.chatbot.dto.ChatResponse;
import com.ahnis.journalai.chatbot.dto.ChatStreamRequest;
import com.ahnis.journalai.chatbot.dto.PaginatedChatSessions;
import com.ahnis.journalai.chatbot.entity.ChatSession;
import com.ahnis.journalai.chatbot.service.ChatService;
import com.ahnis.journalai.chatbot.service.ChatSessionService;
import com.ahnis.journalai.chatbot.dto.ChatRequest;
import com.ahnis.journalai.common.dto.ApiResponse;
import com.ahnis.journalai.user.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v2/chat")
public class ChatBotBetaController {
    private final ChatService chatService;
    private final ChatSessionService chatSessionService;

    public ChatBotBetaController(@Qualifier("chatServiceBetaImpl") ChatService chatService, ChatSessionService chatSessionService) {
        this.chatService = chatService;
        this.chatSessionService = chatSessionService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal User user) {
        var response = chatService.chatSync(user, chatRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/s/{sessionId}")
    public Flux<String> chatStream(
            @PathVariable String sessionId,
            @RequestBody ChatStreamRequest chatRequest,
            @AuthenticationPrincipal User user) {
        return chatService.chatFlux(chatRequest, sessionId, user);
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<PaginatedChatSessions>> getUserSessions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String[] sort) {

        // Create pageable with sorting
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<ChatSession> sessionPage = chatSessionService.getUserSessionsPaginated(user.getId(), pageable);
        PaginatedChatSessions response = PaginatedChatSessions.fromPage(sessionPage);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/s/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal User user) {
        chatSessionService.deleteSession(sessionId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
