package com.leadsubmission.observability.repository.projection;

/**
 * Failure reason → count aggregation.
 */
public interface FailureReasonCountProjection {

    String getReason();

    Long getCount();
}
