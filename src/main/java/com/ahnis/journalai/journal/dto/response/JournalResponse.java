package com.ahnis.journalai.journal.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.ZonedDateTime;

public record JournalResponse(
        String id,
        String title,
        String content,

        Instant createdAt,

        Instant modifiedAt,
        String userId
) {

}
