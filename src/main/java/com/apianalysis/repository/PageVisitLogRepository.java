package com.apianalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apianalysis.entity.PageVisitLog;

@Repository
public interface PageVisitLogRepository extends JpaRepository<PageVisitLog, Long> {
}
