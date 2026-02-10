package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.dto.LeadRequest;
import com.leadsubmission.observability.dto.LeadResponse;
import com.leadsubmission.observability.entity.User;
import com.leadsubmission.observability.service.AuthService;
import com.leadsubmission.observability.service.LeadAuditService;
import com.leadsubmission.observability.service.LeadSubmissionService;
import com.leadsubmission.observability.service.RateLimitService;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
// @CrossOrigin can be removed if frontend is now served by Spring Boot
public class LeadController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;
    private final LeadSubmissionService leadSubmissionService;
    private final LeadAuditService leadAuditService;

    public LeadController(
            AuthService authService,
            RateLimitService rateLimitService,
            LeadSubmissionService leadSubmissionService,
            LeadAuditService leadAuditService
    ) {
        this.authService = authService;
        this.rateLimitService = rateLimitService;
        this.leadSubmissionService = leadSubmissionService;
        this.leadAuditService = leadAuditService;
    }

    @PostMapping
    public ResponseEntity<LeadResponse> submitLead(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Password") String userPassword,
            @Valid @RequestBody LeadRequest leadRequest
    ) {

        // 1️⃣ Authenticate
        User user = authService.authenticate(userEmail, userPassword);

        // 2️⃣ Rate-limit enforcement (HARD GATE)
        rateLimitService.checkAndConsume(user.getId());

        try {
            // 3️⃣ Transactional business operation
            leadSubmissionService.submitLead(user, leadRequest);

            return ResponseEntity.ok(
                    new LeadResponse("SUCCESS", "Lead submitted successfully")
            );

        } catch (DataIntegrityViolationException ex) {

            // 🔍 Audit DUPLICATE outside the poisoned transaction
            leadAuditService.logAttempt(
                    user.getId(),
                    user.getEmail(),
                    leadRequest.getName(),
                    leadRequest.getEmail(),
                    leadRequest.getSource(),
                    leadRequest.getFinalPage(),
                    "FAILED",
                    "DUPLICATE_LEAD"
            );

            rateLimitService.recordFailure(user.getId());

            return ResponseEntity
                    .status(409)
                    .body(new LeadResponse(
                            "DUPLICATE_LEAD",
                            "Lead already submitted for this page"
                    ));
        }
    }
}
