package com.leadsubmission.observability.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "user_rate_limits")
public class UserRateLimit {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "rate_limit", nullable = false)
    private Integer rateLimit;

    @Column(name = "window_seconds", nullable = false)
    private Integer windowSeconds;

    @Column(name = "current_count", nullable = false)
    private Integer currentCount;

    @Column(name = "window_start")
    private LocalDateTime windowStart;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    /**
     * Database-generated column:
     * total_count = success_count + failure_count
     * READ-ONLY from JPA perspective
     */
    @Column(name = "total_count", insertable = false, updatable = false)
    private Integer totalCount;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─────────────────────────────────────
    // JPA lifecycle hooks (IST-correct)
    // ─────────────────────────────────────

    @PrePersist
    @PreUpdate
    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now(IST_ZONE);
    }

    // ─────────────────────────────────────
    // Factory method for new users
    // ─────────────────────────────────────

    public static UserRateLimit createDefault(Long userId) {
        UserRateLimit rl = new UserRateLimit();
        rl.userId = userId;
        rl.rateLimit = 100;
        rl.windowSeconds = 60;
        rl.currentCount = 0;
        rl.successCount = 0;
        rl.failureCount = 0;
        rl.windowStart = LocalDateTime.now(IST_ZONE);
        return rl;
    }

    // getters & setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(Integer rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Integer getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(Integer windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
