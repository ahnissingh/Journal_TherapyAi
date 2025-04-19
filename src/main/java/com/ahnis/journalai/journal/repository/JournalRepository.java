package com.ahnis.journalai.journal.repository;

import com.ahnis.journalai.journal.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalRepository extends MongoRepository<Journal, String> {
    Page<Journal> findByUserId(String userId, Pageable pageable);


}

