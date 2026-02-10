package com.apianalysis.analysis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_analysis_reports",
        uniqueConstraints = @UniqueConstraint(columnNames = "day")
)
public class DailyAnalysisReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate day;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String executiveSummary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String userBehaviorAnalysis;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String systemBehaviorAnalysis;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String misconfigurationSignals;

    @Column(nullable = false)
    private String analysisConfidence;

    @Column(nullable = false)
    private String analysisScopeNote;

    protected DailyAnalysisReportEntity() {}

    public DailyAnalysisReportEntity(
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

    // Getters only (immutable)

    public Long getId() { return id; }
    public LocalDate getDay() { return day; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getExecutiveSummary() { return executiveSummary; }
    public String getUserBehaviorAnalysis() { return userBehaviorAnalysis; }
    public String getSystemBehaviorAnalysis() { return systemBehaviorAnalysis; }
    public String getMisconfigurationSignals() { return misconfigurationSignals; }
    public String getAnalysisConfidence() { return analysisConfidence; }
    public String getAnalysisScopeNote() { return analysisScopeNote; }
}
