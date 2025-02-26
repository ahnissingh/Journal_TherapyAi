package com.ahnis.journalai.journal.repository;

import com.ahnis.journalai.journal.entity.Journal;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface JournalRepository extends MongoRepository<Journal, String> {
    List<Journal> findByUserId(String userId);

}
