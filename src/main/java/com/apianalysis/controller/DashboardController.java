package com.apianalysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apianalysis.dto.DashboardSummaryResponse;
import com.apianalysis.repository.projection.DailySuccessFailureProjection;
import com.apianalysis.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ------------------------------------------------------------
    // Dashboard Summary (Overall + Today)
    // ------------------------------------------------------------

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }

    // ------------------------------------------------------------
    // Dashboard Graph: Last 7 Days Success vs Failure
    // ------------------------------------------------------------

    @GetMapping("/daily-trends")
    public List<DailySuccessFailureProjection> getDailyTrends() {
        return dashboardService.getDailyTrendsLast7Days();
    }
}
