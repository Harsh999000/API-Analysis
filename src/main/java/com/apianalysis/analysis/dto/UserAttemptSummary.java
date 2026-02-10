package com.apianalysis.analysis.dto;

/**
 * Aggregate user behavior summary.
 * Used only for analysis (not auth, not domain logic).
 */
public final class UserAttemptSummary {

    private final String userEmail;
    private final long attempts;
    private final long failures;
    private final double failureRatio;

    public UserAttemptSummary(
            String userEmail,
            long attempts,
            long failures
    ) {
        this.userEmail = userEmail;
        this.attempts = attempts;
        this.failures = failures;
        this.failureRatio = attempts == 0
                ? 0.0
                : (double) failures / attempts;
    }

    public String getUserEmail() { return userEmail; }
    public long getAttempts() { return attempts; }
    public long getFailures() { return failures; }
    public double getFailureRatio() { return failureRatio; }
}
