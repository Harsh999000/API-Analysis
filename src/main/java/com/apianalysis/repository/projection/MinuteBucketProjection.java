package com.apianalysis.repository.projection;

/**
 * Minute-bucketed attempt count (YYYY-MM-DD HH:mm).
 */
public interface MinuteBucketProjection {

    String getMinuteBucket();

    Long getCount();
}
