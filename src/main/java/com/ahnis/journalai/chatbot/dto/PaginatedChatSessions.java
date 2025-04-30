package com.ahnis.journalai.chatbot.dto;


import com.ahnis.journalai.chatbot.entity.ChatSession;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PaginatedChatSessions {
    private List<ChatSession> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private boolean first;
    private boolean last;

    public static PaginatedChatSessions fromPage(Page<ChatSession> page) {
        return PaginatedChatSessions.builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
