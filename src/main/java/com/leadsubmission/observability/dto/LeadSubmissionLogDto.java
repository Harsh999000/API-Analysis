package com.leadsubmission.observability.dto;

import java.time.LocalDateTime;

public class LeadSubmissionLogDto {

    private final String userEmail;
    private final String leadName;
    private final String leadEmail;
    private final String source;
    private final String finalPage;
    private final String outcome;
    private final String failureReason;
    private final LocalDateTime createdAt;

    public LeadSubmissionLogDto(
            String userEmail,
            String leadName,
            String leadEmail,
            String source,
            String finalPage,
            String outcome,
            String failureReason,
            LocalDateTime createdAt
    ) {
        this.userEmail = userEmail;
        this.leadName = leadName;
        this.leadEmail = leadEmail;
        this.source = source;
        this.finalPage = finalPage;
        this.outcome = outcome;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public String getUserEmail() { return userEmail; }
    public String getLeadName() { return leadName; }
    public String getLeadEmail() { return leadEmail; }
    public String getSource() { return source; }
    public String getFinalPage() { return finalPage; }
    public String getOutcome() { return outcome; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
