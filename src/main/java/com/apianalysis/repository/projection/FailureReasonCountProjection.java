package com.apianalysis.repository.projection;

/**
 * Failure reason → count aggregation.
 */
public interface FailureReasonCountProjection {

    String getReason();

    Long getCount();
}
