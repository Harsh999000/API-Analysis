package com.leadsubmission.observability.analysis.service;

import com.leadsubmission.observability.analysis.dto.DailyAnalysisReport;
import com.leadsubmission.observability.analysis.entity.DailyAnalysisReportEntity;
import org.springframework.stereotype.Component;

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
