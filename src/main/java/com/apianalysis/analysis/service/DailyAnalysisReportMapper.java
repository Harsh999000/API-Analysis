package com.apianalysis.analysis.service;

import org.springframework.stereotype.Component;

import com.apianalysis.analysis.dto.DailyAnalysisReport;
import com.apianalysis.analysis.entity.DailyAnalysisReportEntity;

@Component
public class DailyAnalysisReportMapper {

    public DailyAnalysisReportEntity toEntity(DailyAnalysisReport report) {
        return new DailyAnalysisReportEntity(
                report.getDay(),
                report.getGeneratedAt(),
                report.getExecutiveSummary(),
                report.getUserBehaviorAnalysis(),
                report.getSystemBehaviorAnalysis(),
                report.getMisconfigurationSignals(),
                report.getAnalysisConfidence(),
                report.getAnalysisScopeNote()
        );
    }
}
