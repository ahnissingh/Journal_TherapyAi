package com.ahnis.journalai.journal.entity;

import lombok.*;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "journals")
public class Journal {

    @Id
    private String id;
    private String title;
    private String content;
    @Indexed
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;

    @Indexed
    private String userId; //added index for user id
}

