package com.ahnis.journalai.chatbot.service;


import com.ahnis.journalai.chatbot.entity.ChatSession;
import com.ahnis.journalai.chatbot.dto.ChatMessage;
import com.ahnis.journalai.chatbot.exception.InvalidSessionException;
import com.ahnis.journalai.chatbot.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;

    public ChatSession createNewSession(String userId) {
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return chatSessionRepository.save(session);
    }

    public ChatSession getSession(String sessionId, String userId) {
        return chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    public Page<ChatSession> getUserSessionsPaginated(String userId, Pageable pageable) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId,pageable);
    }

    public ChatSession addUserMessage(String sessionId, String userId, String message) {
        ChatSession session = getSession(sessionId, userId);
        session.addMessage(ChatMessage.builder()
                .role("USER")
                .content(message)
                .timestamp(Instant.now())
                .build());
        return chatSessionRepository.save(session);
    }

    public ChatSession addAssistantMessage(String sessionId, String userId, String message) {
        ChatSession session = getSession(sessionId, userId);
        session.addMessage(ChatMessage.builder()
                .role("ASSISTANT")
                .content(message)
                .timestamp(Instant.now())
                .build());
        return chatSessionRepository.save(session);
    }

    public void deleteSession(String sessionId, String userId) {
        if (!chatSessionRepository.existsByIdAndUserId(sessionId, userId)) {
            throw new InvalidSessionException("Invalid session ID for user");
        }
        chatSessionRepository.deleteById(sessionId);
    }

    public boolean isValidSession(String sessionId, String userId) {
        return chatSessionRepository.existsByIdAndUserId(sessionId, userId);
    }
}
