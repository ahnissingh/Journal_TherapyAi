package com.ahnis.journalai.mapper;

import com.ahnis.journalai.dto.journal.JournalRequest;
import com.ahnis.journalai.dto.journal.JournalResponse;
import com.ahnis.journalai.entity.Journal;
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
