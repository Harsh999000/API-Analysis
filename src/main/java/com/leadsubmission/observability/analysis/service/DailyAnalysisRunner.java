package com.leadsubmission.observability.analysis.service;

import com.leadsubmission.observability.analysis.dto.DailyAnalysisReport;
import com.leadsubmission.observability.analysis.dto.DailyAnalysisSnapshot;
import com.leadsubmission.observability.analysis.entity.DailyAnalysisReportEntity;
import com.leadsubmission.observability.analysis.repository.DailyAnalysisReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Orchestrates the daily analysis flow.
 *
 * Policy:
 * - Past days (before today IST): immutable → return existing
 * - Today & future: mutable → overwrite if exists
 */
@Service
public class DailyAnalysisRunner {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    private final DailyAnalysisSnapshotAssembler snapshotAssembler;
    private final AnalysisPromptBuilder promptBuilder;
    private final AiAnalysisService aiAnalysisService;
    private final DailyAnalysisReportRepository reportRepository;
    private final DailyAnalysisReportMapper reportMapper;

    public DailyAnalysisRunner(
            DailyAnalysisSnapshotAssembler snapshotAssembler,
            AnalysisPromptBuilder promptBuilder,
            AiAnalysisService aiAnalysisService,
            DailyAnalysisReportRepository reportRepository,
            DailyAnalysisReportMapper reportMapper
    ) {
        this.snapshotAssembler = snapshotAssembler;
        this.promptBuilder = promptBuilder;
        this.aiAnalysisService = aiAnalysisService;
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    public DailyAnalysisReport runForDay(LocalDate day) {

        LocalDate todayIst = LocalDate.now(IST_ZONE);

        Optional<DailyAnalysisReportEntity> existing =
                reportRepository.findByDay(day);

        // Past day & exists → immutable
        if (existing.isPresent() && day.isBefore(todayIst)) {
            return toDto(existing.get());
        }

        // Today or future → overwrite allowed
        existing.ifPresent(reportRepository::delete);

        return runAndPersist(day);
    }

    private DailyAnalysisReport runAndPersist(LocalDate day) {

        // Assemble factual snapshot
        DailyAnalysisSnapshot snapshot =
                snapshotAssembler.assembleForDay(day);

        // Build bounded AI prompt
        String prompt =
                promptBuilder.buildPrompt(snapshot);

        // Perform AI analysis
        DailyAnalysisReport report =
                aiAnalysisService.analyze(day, prompt);

        // Persist fresh report
        DailyAnalysisReportEntity entity =
                reportMapper.toEntity(report);

        reportRepository.save(entity);

        return report;
    }

    private DailyAnalysisReport toDto(DailyAnalysisReportEntity entity) {
        return new DailyAnalysisReport(
                entity.getDay(),
                entity.getGeneratedAt(),
                entity.getExecutiveSummary(),
                entity.getUserBehaviorAnalysis(),
                entity.getSystemBehaviorAnalysis(),
                entity.getMisconfigurationSignals(),
                entity.getAnalysisConfidence(),
                entity.getAnalysisScopeNote()
        );
    }
}
