package com.ahnis.journalai.repository;

import com.ahnis.journalai.entity.Journal;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface JournalRepository extends MongoRepository<Journal, String> {
    List<Journal> findByUserId(String userId);

}
