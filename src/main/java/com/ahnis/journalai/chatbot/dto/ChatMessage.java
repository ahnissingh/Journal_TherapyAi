package com.ahnis.journalai.chatbot.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String role; // "USER" or "ASSISTANT"
    private String content;
    private Instant timestamp;
}
