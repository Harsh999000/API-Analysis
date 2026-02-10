package com.apianalysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apianalysis.dto.LeadLogDto;
import com.apianalysis.service.LeadLogService;

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
