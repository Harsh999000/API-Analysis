package com.apianalysis.analysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.analysis.dto.DailyAnalysisSnapshot;
import com.apianalysis.analysis.dto.UserAttemptSummary;
import com.apianalysis.repository.LeadSubmissionLogRepository;
import com.apianalysis.repository.projection.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Assembles a fully materialized DailyAnalysisSnapshot for a single IST day.
 * Read-only. Deterministic. No AI logic.
 */
@Service
public class DailyAnalysisSnapshotAssembler {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    // ---- Tunable thresholds (locked for now) ----
    private static final long MIN_ATTEMPTS_FOR_USER_ANALYSIS = 10;
    private static final long BURST_THRESHOLD_PER_MINUTE = 20;

    private final LeadSubmissionLogRepository logRepository;

    public DailyAnalysisSnapshotAssembler(LeadSubmissionLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public DailyAnalysisSnapshot assembleForDay(LocalDate day) {

        // ---- Day window (IST, half-open) ----
        LocalDateTime dayStart = day.atStartOfDay(IST_ZONE).toLocalDateTime();
        LocalDateTime dayEnd = day.plusDays(1).atStartOfDay(IST_ZONE).toLocalDateTime();

        // ---- Volume ----
        long totalAttempts = logRepository.countTotalAttempts(dayStart, dayEnd);

        Map<String, Long> outcomeCounts = logRepository
                .countByOutcome(dayStart, dayEnd)
                .stream()
                .collect(Collectors.toMap(
                        OutcomeCountProjection::getOutcome,
                        OutcomeCountProjection::getCount
                ));

        long successCount = outcomeCounts.getOrDefault("SUCCESS", 0L);
        long failureCount = outcomeCounts.getOrDefault("FAILED", 0L);

        double successRatio = totalAttempts == 0
                ? 0.0
                : (double) successCount / totalAttempts;

        // ---- Failure shape ----
        Map<String, Long> failuresByReason = logRepository
                .countFailuresByReason(dayStart, dayEnd)
                .stream()
                .collect(Collectors.toMap(
                        FailureReasonCountProjection::getReason,
                        FailureReasonCountProjection::getCount
                ));

        // ---- User behavior ----
        List<UserAttemptSummary> highActivityUsers = logRepository
                .userAttemptStats(dayStart, dayEnd, MIN_ATTEMPTS_FOR_USER_ANALYSIS)
                .stream()
                .map(p -> new UserAttemptSummary(
                        p.getUserEmail(),
                        p.getAttempts(),
                        p.getFailures()
                ))
                .collect(Collectors.toList());

        // ---- Source & page signals ----
        Map<String, Long> attemptsBySource = logRepository
                .countBySource(dayStart, dayEnd)
                .stream()
                .collect(Collectors.toMap(
                        SourceCountProjection::getSource,
                        SourceCountProjection::getCount
                ));

        Map<String, Long> duplicatesBySource = logRepository
                .countDuplicatesBySource(dayStart, dayEnd)
                .stream()
                .collect(Collectors.toMap(
                        SourceCountProjection::getSource,
                        SourceCountProjection::getCount
                ));

        Map<String, Long> rateLimitsByFinalPage = logRepository
                .countRateLimitsByFinalPage(dayStart, dayEnd)
                .stream()
                .collect(Collectors.toMap(
                        PageCountProjection::getFinalPage,
                        PageCountProjection::getCount
                ));

        // ---- Temporal shape (burst detection) ----
        List<MinuteBucketProjection> minuteBuckets =
                logRepository.countAttemptsPerMinute(dayStart, dayEnd);

        long maxAttemptsPerMinute = minuteBuckets.stream()
                .mapToLong(MinuteBucketProjection::getCount)
                .max()
                .orElse(0L);

        long burstMinuteCount = minuteBuckets.stream()
                .filter(b -> b.getCount() >= BURST_THRESHOLD_PER_MINUTE)
                .count();

        // ---- Assemble immutable snapshot ----
        return new DailyAnalysisSnapshot(
                day,
                dayStart,
                dayEnd,
                totalAttempts,
                successCount,
                failureCount,
                successRatio,
                failuresByReason,
                highActivityUsers,
                attemptsBySource,
                duplicatesBySource,
                rateLimitsByFinalPage,
                maxAttemptsPerMinute,
                burstMinuteCount
        );
    }
}
