package com.leadsubmission.observability.service;

import com.leadsubmission.observability.dto.LeadRequest;
import com.leadsubmission.observability.entity.Lead;
import com.leadsubmission.observability.entity.User;
import com.leadsubmission.observability.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadSubmissionService {

    private final LeadRepository leadRepository;
    private final LeadAuditService leadAuditService;
    private final RateLimitService rateLimitService;

    public LeadSubmissionService(
            LeadRepository leadRepository,
            LeadAuditService leadAuditService,
            RateLimitService rateLimitService
    ) {
        this.leadRepository = leadRepository;
        this.leadAuditService = leadAuditService;
        this.rateLimitService = rateLimitService;
    }

    /**
     * Single transactional business operation:
     * Submit a lead + audit + rate-limit accounting.
     *
     * IMPORTANT:
     * This method must NOT handle failure logging.
     * Any persistence exception poisons the transaction.
     */
    @Transactional
    public void submitLead(User user, LeadRequest leadRequest) {

        // 1. Persist lead (DB enforces uniqueness)
        Lead lead = new Lead();
        lead.setUserId(user.getId());
        lead.setName(leadRequest.getName());
        lead.setEmail(leadRequest.getEmail());
        lead.setSource(leadRequest.getSource());
        lead.setFinalPage(leadRequest.getFinalPage());

        leadRepository.save(lead);

        // 2. Audit SUCCESS (safe: no exception occurred)
        leadAuditService.logAttempt(
                user.getId(),
                user.getEmail(),
                leadRequest.getName(),
                leadRequest.getEmail(),
                leadRequest.getSource(),
                leadRequest.getFinalPage(),
                "SUCCESS",
                null
        );

        // 3. Rate-limit SUCCESS accounting
        rateLimitService.recordSuccess(user.getId());
    }
}
