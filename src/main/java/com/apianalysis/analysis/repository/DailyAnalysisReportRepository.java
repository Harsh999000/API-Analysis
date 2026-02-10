package com.apianalysis.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apianalysis.analysis.entity.DailyAnalysisReportEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyAnalysisReportRepository
        extends JpaRepository<DailyAnalysisReportEntity, Long> {

    Optional<DailyAnalysisReportEntity> findByDay(LocalDate day);
}
