package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.dto.LeadSubmissionLogDto;
import com.leadsubmission.observability.service.LeadSubmissionLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs/submissions")
public class LeadSubmissionLogsController {

    private final LeadSubmissionLogService service;

    public LeadSubmissionLogsController(LeadSubmissionLogService service) {
        this.service = service;
    }

    // Table view (last 20)
    @GetMapping
    public List<LeadSubmissionLogDto> getLatest(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.getLatestSubmissions(limit);
    }

    // Export view (ALL)
    @GetMapping("/export")
    public List<LeadSubmissionLogDto> exportAll() {
        return service.getLatestSubmissions(Integer.MAX_VALUE);
    }
}
