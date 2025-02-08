package com.ahnis.journalai.repository;

import com.ahnis.journalai.entity.Journal;

import java.util.List;

public interface JournalRepository {
    List<Journal> findByUser_Id(String userId);

}
