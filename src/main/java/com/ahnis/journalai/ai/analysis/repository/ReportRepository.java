package com.ahnis.journalai.ai.analysis.repository;

import com.ahnis.journalai.ai.analysis.entity.MoodReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<MoodReportEntity, String> {
    List<MoodReportEntity> findByUserIdOrderByReportDateDesc(String userId);
}
