package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.dto.LeadLogDto;
import com.leadsubmission.observability.service.LeadLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs/leads")
public class LeadLogsController {

    private final LeadLogService leadLogService;

    public LeadLogsController(LeadLogService leadLogService) {
        this.leadLogService = leadLogService;
    }

    @GetMapping
    public List<LeadLogDto> getLatestLeads(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return leadLogService.getLatestLeads(limit);
    }

    @GetMapping("/export")
    public List<LeadLogDto> exportAllLeads() {
        return leadLogService.getAllLeads();
    }
}
