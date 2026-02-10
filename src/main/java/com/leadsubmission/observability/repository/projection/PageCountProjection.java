package com.leadsubmission.observability.repository.projection;

/**
 * Final page → count aggregation.
 */
public interface PageCountProjection {

    String getFinalPage();

    Long getCount();
}
