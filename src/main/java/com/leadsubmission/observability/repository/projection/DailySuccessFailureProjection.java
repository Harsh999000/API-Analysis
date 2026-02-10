package com.leadsubmission.observability.repository.projection;

/**
 * Projection for dashboard daily trend graph:
 * SUCCESS vs FAILED counts per day
 */
public interface DailySuccessFailureProjection {

    /**
     * Date bucket (YYYY-MM-DD, IST)
     */
    String getDay();

    /**
     * Number of successful submissions for the day
     */
    Long getSuccessCount();

    /**
     * Number of failed submissions for the day
     */
    Long getFailureCount();
}
