package com.leadsubmission.observability.repository.projection;

/**
 * Source → count aggregation.
 */
public interface SourceCountProjection {

    String getSource();

    Long getCount();
}
