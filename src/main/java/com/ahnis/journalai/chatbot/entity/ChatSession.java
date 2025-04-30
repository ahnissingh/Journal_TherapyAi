package com.ahnis.journalai.chatbot.entity;


import com.ahnis.journalai.chatbot.dto.ChatMessage;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_sessions")

public class ChatSession {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    // Helper method to add a message
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.updatedAt = Instant.now();
    }

    // Generate a title from the first message if not provided
    public String getTitle() {
        if (title != null && !title.isEmpty()) {
            return title;
        }
        if (!messages.isEmpty()) {
            String firstMessage = messages.get(0).getContent();
            return firstMessage.length() > 30
                    ? firstMessage.substring(0, 30) + "..."
                    : firstMessage;
        }
        return "New Chat";
    }
}

