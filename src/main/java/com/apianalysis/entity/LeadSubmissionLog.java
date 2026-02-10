package com.apianalysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "lead_submission_logs")
public class LeadSubmissionLog {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userEmail;

    private String leadName;
    private String leadEmail;

    private String source;
    private String finalPage;

    @Column(nullable = false)
    private String outcome;

    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        // Explicit IST timestamp — no reliance on OS defaults
        this.createdAt = LocalDateTime.now(IST_ZONE);
    }

    // Getters only (audit logs should be immutable)

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getLeadName() { return leadName; }
    public String getLeadEmail() { return leadEmail; }
    public String getSource() { return source; }
    public String getFinalPage() { return finalPage; }
    public String getOutcome() { return outcome; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters used only during creation

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setLeadName(String leadName) { this.leadName = leadName; }
    public void setLeadEmail(String leadEmail) { this.leadEmail = leadEmail; }
    public void setSource(String source) { this.source = source; }
    public void setFinalPage(String finalPage) { this.finalPage = finalPage; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
