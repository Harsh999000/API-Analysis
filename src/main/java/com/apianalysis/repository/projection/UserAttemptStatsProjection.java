package com.apianalysis.repository.projection;

/**
 * Per-user attempt and failure aggregation.
 */
public interface UserAttemptStatsProjection {

    String getUserEmail();

    Long getAttempts();

    Long getFailures();
}
