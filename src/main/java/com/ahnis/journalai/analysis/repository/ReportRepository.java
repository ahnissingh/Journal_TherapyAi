package com.ahnis.journalai.analysis.repository;

import com.ahnis.journalai.analysis.entity.MoodReportEntity;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<MoodReportEntity, String> {

    Page<MoodReportEntity> findByUserId(String userId, Pageable pageable);

    Optional<MoodReportEntity> findByIdAndUserId(String id, String userId);

    Optional<MoodReportEntity> findFirstByUserIdOrderByReportDateDesc(String userId);
}
