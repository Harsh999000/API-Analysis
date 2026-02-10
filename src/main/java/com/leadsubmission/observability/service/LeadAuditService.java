package com.leadsubmission.observability.service;

import com.leadsubmission.observability.entity.LeadSubmissionLog;
import com.leadsubmission.observability.repository.LeadSubmissionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadAuditService {

    private final LeadSubmissionLogRepository logRepository;

    public LeadAuditService(LeadSubmissionLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Core audit logging method.
     * This will be used by /api/leads for every attempt (success or failure).
     *
     * IMPORTANT:
     * Runs in a separate transaction so audit logging
     * is not affected by business transaction rollback.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAttempt(
            Long userId,
            String userEmail,
            String leadName,
            String leadEmail,
            String source,
            String finalPage,
            String outcome,
            String failureReason
    ) {
        LeadSubmissionLog log = new LeadSubmissionLog();

        log.setUserId(userId);
        log.setUserEmail(userEmail);
        log.setLeadName(leadName);
        log.setLeadEmail(leadEmail);
        log.setSource(source);
        log.setFinalPage(finalPage);
        log.setOutcome(outcome);
        log.setFailureReason(failureReason);

        logRepository.save(log);
    }
}
