package com.apianalysis.analysis.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Human-readable AI analysis output for a single IST day.
 * This is an explanation of what happened, not a decision or prediction.
 */
public final class DailyAnalysisReport {

    // ---- Metadata ----
    private final LocalDate day;
    private final LocalDateTime generatedAt;

    // ---- Summary ----
    private final String executiveSummary;

    // ---- Detailed Sections ----
    private final String userBehaviorAnalysis;
    private final String systemBehaviorAnalysis;
    private final String misconfigurationSignals;

    // ---- Confidence & Scope ----
    private final String analysisConfidence;
    private final String analysisScopeNote;

    public DailyAnalysisReport(
            LocalDate day,
            LocalDateTime generatedAt,
            String executiveSummary,
            String userBehaviorAnalysis,
            String systemBehaviorAnalysis,
            String misconfigurationSignals,
            String analysisConfidence,
            String analysisScopeNote
    ) {
        this.day = day;
        this.generatedAt = generatedAt;
        this.executiveSummary = executiveSummary;
        this.userBehaviorAnalysis = userBehaviorAnalysis;
        this.systemBehaviorAnalysis = systemBehaviorAnalysis;
        this.misconfigurationSignals = misconfigurationSignals;
        this.analysisConfidence = analysisConfidence;
        this.analysisScopeNote = analysisScopeNote;
    }

    public LocalDate getDay() {
        return day;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public String getUserBehaviorAnalysis() {
        return userBehaviorAnalysis;
    }

    public String getSystemBehaviorAnalysis() {
        return systemBehaviorAnalysis;
    }

    public String getMisconfigurationSignals() {
        return misconfigurationSignals;
    }

    public String getAnalysisConfidence() {
        return analysisConfidence;
    }

    public String getAnalysisScopeNote() {
        return analysisScopeNote;
    }
}
