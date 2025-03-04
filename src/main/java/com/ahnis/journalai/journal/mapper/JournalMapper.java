package com.ahnis.journalai.journal.mapper;

import com.ahnis.journalai.journal.dto.request.JournalRequest;
import com.ahnis.journalai.journal.dto.response.JournalResponse;
import com.ahnis.journalai.journal.entity.Journal;
import org.mapstruct.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface JournalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Journal toEntity(JournalRequest dto, String userId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    @Mapping(target = "userId", source = "userId")
    JournalResponse toDto(Journal journal);

}
