package com.leadsubmission.observability.analysis.repository;

import com.leadsubmission.observability.analysis.entity.DailyAnalysisReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyAnalysisReportRepository
        extends JpaRepository<DailyAnalysisReportEntity, Long> {

    Optional<DailyAnalysisReportEntity> findByDay(LocalDate day);
}
