package com.leadsubmission.observability.repository;

import com.leadsubmission.observability.entity.PageVisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageVisitLogRepository extends JpaRepository<PageVisitLog, Long> {
}
