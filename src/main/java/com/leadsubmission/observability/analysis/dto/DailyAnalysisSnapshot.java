package com.leadsubmission.observability.analysis.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Immutable, day-scoped aggregate snapshot.
 * Input to AI analysis layer. No raw rows.
 */
public final class DailyAnalysisSnapshot {

    // ---- Day Metadata ----
    private final LocalDate day;
    private final LocalDateTime dayStart;
    private final LocalDateTime dayEnd;

    // ---- Volume ----
    private final long totalAttempts;
    private final long successCount;
    private final long failureCount;
    private final double successRatio;

    // ---- Failure Shape ----
    private final Map<String, Long> failuresByReason;

    // ---- User Behavior ----
    private final List<UserAttemptSummary> highActivityUsers;

    // ---- Source & Page Signals ----
    private final Map<String, Long> attemptsBySource;
    private final Map<String, Long> duplicatesBySource;
    private final Map<String, Long> rateLimitsByFinalPage;

    // ---- Temporal Shape ----
    private final long maxAttemptsPerMinute;
    private final long burstMinuteCount;

    public DailyAnalysisSnapshot(
            LocalDate day,
            LocalDateTime dayStart,
            LocalDateTime dayEnd,
            long totalAttempts,
            long successCount,
            long failureCount,
            double successRatio,
            Map<String, Long> failuresByReason,
            List<UserAttemptSummary> highActivityUsers,
            Map<String, Long> attemptsBySource,
            Map<String, Long> duplicatesBySource,
            Map<String, Long> rateLimitsByFinalPage,
            long maxAttemptsPerMinute,
            long burstMinuteCount
    ) {
        this.day = day;
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.totalAttempts = totalAttempts;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.successRatio = successRatio;

        // ---- FULL normalization ----
        this.failuresByReason = normalizeMap(failuresByReason);
        this.highActivityUsers =
                highActivityUsers == null ? List.of() : List.copyOf(highActivityUsers);
        this.attemptsBySource = normalizeMap(attemptsBySource);
        this.duplicatesBySource = normalizeMap(duplicatesBySource);
        this.rateLimitsByFinalPage = normalizeMap(rateLimitsByFinalPage);

        this.maxAttemptsPerMinute = maxAttemptsPerMinute;
        this.burstMinuteCount = burstMinuteCount;
    }

    /**
     * Converts null map OR maps with null keys/values into a safe, immutable map.
     */
    private static Map<String, Long> normalizeMap(Map<String, Long> input) {
        if (input == null || input.isEmpty()) {
            return Map.of();
        }

        return input.entrySet()
                .stream()
                .filter(e -> e.getKey() != null)
                .map(e -> Map.entry(
                        e.getKey(),
                        e.getValue() == null ? 0L : e.getValue()
                ))
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Long::sum
                ));
    }

    public LocalDate getDay() { return day; }
    public LocalDateTime getDayStart() { return dayStart; }
    public LocalDateTime getDayEnd() { return dayEnd; }

    public long getTotalAttempts() { return totalAttempts; }
    public long getSuccessCount() { return successCount; }
    public long getFailureCount() { return failureCount; }
    public double getSuccessRatio() { return successRatio; }

    public Map<String, Long> getFailuresByReason() { return failuresByReason; }
    public List<UserAttemptSummary> getHighActivityUsers() { return highActivityUsers; }

    public Map<String, Long> getAttemptsBySource() { return attemptsBySource; }
    public Map<String, Long> getDuplicatesBySource() { return duplicatesBySource; }
    public Map<String, Long> getRateLimitsByFinalPage() { return rateLimitsByFinalPage; }

    public long getMaxAttemptsPerMinute() { return maxAttemptsPerMinute; }
    public long getBurstMinuteCount() { return burstMinuteCount; }
}
