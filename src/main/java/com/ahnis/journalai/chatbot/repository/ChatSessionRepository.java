package com.ahnis.journalai.chatbot.repository;



import com.ahnis.journalai.chatbot.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    Page<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId, Pageable pageable);
    boolean existsByIdAndUserId(String id, String userId);

    Optional<ChatSession> findByIdAndUserId(String id, String userId);
}
