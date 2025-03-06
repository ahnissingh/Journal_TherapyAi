package com.ahnis.journalai.ai.analysis.repository;

import com.ahnis.journalai.ai.analysis.entity.MoodReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<MoodReportEntity, String> {

    List<MoodReportEntity> findByUserId(String userId);

    Optional<MoodReportEntity> findByIdAndUserId(String id, String userId);

    Optional<MoodReportEntity> findFirstByUserIdOrderByReportDateDesc(String userId);
}
