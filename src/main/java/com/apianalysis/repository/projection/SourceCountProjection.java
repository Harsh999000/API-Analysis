package com.apianalysis.repository.projection;

/**
 * Source → count aggregation.
 */
public interface SourceCountProjection {

    String getSource();

    Long getCount();
}
