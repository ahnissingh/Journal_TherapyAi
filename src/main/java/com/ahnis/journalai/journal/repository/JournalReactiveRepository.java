package com.ahnis.journalai.journal.repository;

import com.ahnis.journalai.journal.entity.Journal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface JournalReactiveRepository extends ReactiveMongoRepository<Journal, String> {
}
