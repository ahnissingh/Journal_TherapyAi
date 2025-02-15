package com.ahnis.journalai.mapper;

import com.ahnis.journalai.dto.JournalRequestDTO;
import com.ahnis.journalai.dto.JournalResponseDTO;
import com.ahnis.journalai.entity.Journal;
import com.ahnis.journalai.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JournalMapper {
    public Journal toEntity(JournalRequestDTO dto, User user) {
        return Journal.builder()
                .title(dto.title())
                .content(dto.content())
                .user(user)
                .build();
    }

    public JournalResponseDTO toDto(Journal journal) {
        return new JournalResponseDTO(
                journal.getId(),
                journal.getTitle(),
                journal.getContent(),
                journal.getCreatedAt(),
                journal.getModifiedAt(),
                journal.getUser().getId()
        );
    }
}
