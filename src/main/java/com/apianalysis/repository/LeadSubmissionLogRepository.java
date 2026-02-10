package com.apianalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.apianalysis.entity.LeadSubmissionLog;
import com.apianalysis.repository.projection.*;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadSubmissionLogRepository
        extends JpaRepository<LeadSubmissionLog, Long> {

    // ---- Raw logs (for Logs UI & CSV) ----

    @Query("""
        SELECT l
        FROM LeadSubmissionLog l
        ORDER BY l.createdAt DESC
    """)
    List<LeadSubmissionLog> findAllOrderByCreatedAtDesc();

    // ---- Dashboard summary (global) ----

    @Query("""
        SELECT COUNT(l.id)
        FROM LeadSubmissionLog l
        WHERE l.outcome = 'SUCCESS'
    """)
    long countSuccessfulAttempts();

    // ---- Volume ----

    @Query("""
        SELECT COUNT(l.id)
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
    """)
    long countTotalAttempts(
            LocalDateTime start,
            LocalDateTime end
    );

    // ---- Outcome breakdown ----

    @Query("""
        SELECT
            l.outcome AS outcome,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
        GROUP BY l.outcome
    """)
    List<OutcomeCountProjection> countByOutcome(
            LocalDateTime start,
            LocalDateTime end
    );

    // ---- Failure reasons ----

    @Query("""
        SELECT
            l.failureReason AS reason,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
          AND l.outcome = 'FAILED'
        GROUP BY l.failureReason
    """)
    List<FailureReasonCountProjection> countFailuresByReason(
            LocalDateTime start,
            LocalDateTime end
    );

    // ---- User behavior ----

    @Query("""
        SELECT
            l.userEmail AS userEmail,
            COUNT(l.id) AS attempts,
            SUM(CASE WHEN l.outcome = 'FAILED' THEN 1 ELSE 0 END) AS failures
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
        GROUP BY l.userEmail
        HAVING COUNT(l.id) >= :minAttempts
    """)
    List<UserAttemptStatsProjection> userAttemptStats(
            LocalDateTime start,
            LocalDateTime end,
            long minAttempts
    );

    // ---- Source signals ----

    @Query("""
        SELECT
            l.source AS source,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
        GROUP BY l.source
    """)
    List<SourceCountProjection> countBySource(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT
            l.source AS source,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
          AND l.failureReason = 'DUPLICATE_LEAD'
        GROUP BY l.source
    """)
    List<SourceCountProjection> countDuplicatesBySource(
            LocalDateTime start,
            LocalDateTime end
    );

    // ---- Page-level rate limits ----

    @Query("""
        SELECT
            l.finalPage AS finalPage,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
          AND l.failureReason = 'RATE_LIMIT_EXCEEDED'
        GROUP BY l.finalPage
    """)
    List<PageCountProjection> countRateLimitsByFinalPage(
            LocalDateTime start,
            LocalDateTime end
    );

    // ---- Temporal clustering ----

    @Query("""
        SELECT
            FUNCTION('DATE_FORMAT', l.createdAt, '%Y-%m-%d %H:%i') AS minuteBucket,
            COUNT(l.id) AS count
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
          AND l.createdAt < :end
        GROUP BY minuteBucket
    """)
    List<MinuteBucketProjection> countAttemptsPerMinute(
            LocalDateTime start,
            LocalDateTime end
    );

    // ============================================================
    //  Dashboard: Last 7 Days Success vs Failure Trend
    // ============================================================

    @Query("""
        SELECT
            FUNCTION('DATE', l.createdAt) AS day,
            SUM(CASE WHEN l.outcome = 'SUCCESS' THEN 1 ELSE 0 END) AS successCount,
            SUM(CASE WHEN l.outcome = 'FAILED' THEN 1 ELSE 0 END) AS failureCount
        FROM LeadSubmissionLog l
        WHERE l.createdAt >= :start
        GROUP BY day
        ORDER BY day ASC
    """)
    List<DailySuccessFailureProjection> fetchDailySuccessFailureTrend(
            LocalDateTime start
    );
}
