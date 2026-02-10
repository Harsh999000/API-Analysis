package com.leadsubmission.observability.service;

import com.leadsubmission.observability.entity.UserRateLimit;
import com.leadsubmission.observability.exception.RateLimitExceededException;
import com.leadsubmission.observability.repository.UserRateLimitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class RateLimitService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    private final UserRateLimitRepository repository;

    public RateLimitService(UserRateLimitRepository repository) {
        this.repository = repository;
    }

    /**
     * Enforces rate limit and consumes one request attempt.
     * This MUST be called once per authenticated API request.
     */
    @Transactional
    public void checkAndConsume(Long userId) {

        UserRateLimit limit = repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Rate limit row missing for user"));

        LocalDateTime now = LocalDateTime.now(IST_ZONE);

        // Initialize window if needed
        if (limit.getWindowStart() == null) {
            limit.setWindowStart(now);
            limit.setCurrentCount(0);
        }

        LocalDateTime windowEnd = limit.getWindowStart()
                .plusSeconds(limit.getWindowSeconds());

        // Reset window if expired
        if (now.isAfter(windowEnd)) {
            limit.setWindowStart(now);
            limit.setCurrentCount(0);
        }

        // Enforce limit
        if (limit.getCurrentCount() >= limit.getRateLimit()) {
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }

        // Consume attempt
        limit.setCurrentCount(limit.getCurrentCount() + 1);
        repository.save(limit);
    }

    public void recordSuccess(Long userId) {
        updateCounters(userId, true);
    }

    public void recordFailure(Long userId) {
        updateCounters(userId, false);
    }

    private void updateCounters(Long userId, boolean success) {

        UserRateLimit limit = repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Rate limit row missing for user"));

        if (success) {
            limit.setSuccessCount(limit.getSuccessCount() + 1);
        } else {
            limit.setFailureCount(limit.getFailureCount() + 1);
        }

        repository.save(limit);
    }
}
