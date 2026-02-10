package com.apianalysis.dto;

public class DashboardSummaryResponse {

    // ---- Overall (All Time) ----
    private final long totalUsers;
    private final long totalLeads;
    private final long submissionAttempts;
    private final long successCount;
    private final double successRate;

    // ---- Today ----
    private final long totalLeadsToday;
    private final long submissionAttemptsToday;
    private final long successCountToday;
    private final double successRateToday;

    public DashboardSummaryResponse(
            long totalUsers,
            long totalLeads,
            long submissionAttempts,
            long successCount,
            double successRate,
            long totalLeadsToday,
            long submissionAttemptsToday,
            long successCountToday,
            double successRateToday
    ) {
        this.totalUsers = totalUsers;
        this.totalLeads = totalLeads;
        this.submissionAttempts = submissionAttempts;
        this.successCount = successCount;
        this.successRate = successRate;
        this.totalLeadsToday = totalLeadsToday;
        this.submissionAttemptsToday = submissionAttemptsToday;
        this.successCountToday = successCountToday;
        this.successRateToday = successRateToday;
    }

    // ---- Overall getters ----

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalLeads() {
        return totalLeads;
    }

    public long getSubmissionAttempts() {
        return submissionAttempts;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public double getSuccessRate() {
        return successRate;
    }

    // ---- Today getters ----

    public long getTotalLeadsToday() {
        return totalLeadsToday;
    }

    public long getSubmissionAttemptsToday() {
        return submissionAttemptsToday;
    }

    public long getSuccessCountToday() {
        return successCountToday;
    }

    public double getSuccessRateToday() {
        return successRateToday;
    }
}
