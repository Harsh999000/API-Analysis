package com.leadsubmission.observability.analysis.controller;

import com.leadsubmission.observability.analysis.dto.DailyAnalysisReport;
import com.leadsubmission.observability.analysis.service.DailyAnalysisRunner;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * INTERNAL controller for manually triggering daily AI analysis.
 * This endpoint is used to validate AI output before dashboards & persistence.
 */
@RestController
@RequestMapping("/internal/analysis")
public class InternalAnalysisController {

    private final DailyAnalysisRunner analysisRunner;

    public InternalAnalysisController(DailyAnalysisRunner analysisRunner) {
        this.analysisRunner = analysisRunner;
    }

    /**
     * Run AI analysis for a given IST day.
     * Example:
     * GET /internal/analysis/run?day=2026-01-22
     */
    @GetMapping("/run")
    public DailyAnalysisReport runAnalysis(
            @RequestParam("day")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate day
    ) {
        return analysisRunner.runForDay(day);
    }
}
