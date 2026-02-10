package com.apianalysis.repository.projection;

/**
 * Outcome → count aggregation (SUCCESS / FAILED).
 */
public interface OutcomeCountProjection {

    String getOutcome();

    Long getCount();
}
