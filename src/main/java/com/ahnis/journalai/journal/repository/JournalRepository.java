package com.ahnis.journalai.journal.repository;

import com.ahnis.journalai.journal.entity.Journal;
import com.ahnis.journalai.user.entity.User;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JournalRepository extends MongoRepository<Journal, String> {
    List<Journal> findByUserId(String userId);

    Optional<JournalCreatedAtProjection> findFirstByUserIdOrderByCreatedAtAsc(String userId);

}

