package com.ahnis.journalai.journal.mapper;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.journal.entity.Journal;
import org.springframework.stereotype.Component;

@Component
public class JournalMapper {
    public Journal toEntity(JournalRequest dto, String userId) {
        return Journal.builder()
                .title(dto.title())
                .content(dto.content())
                .userId(userId)
                .build();
    }

    public JournalResponse toDto(Journal journal) {
        return new JournalResponse(
                journal.getId(),
                journal.getTitle(),
                journal.getContent(),
                journal.getCreatedAt(),
                journal.getModifiedAt(),
                journal.getUserId()
        );
    }
}
