package com.leadsubmission.observability.service;

import com.leadsubmission.observability.dto.DashboardSummaryResponse;
import com.leadsubmission.observability.repository.LeadRepository;
import com.leadsubmission.observability.repository.LeadSubmissionLogRepository;
import com.leadsubmission.observability.repository.UserRepository;
import com.leadsubmission.observability.repository.projection.DailySuccessFailureProjection;
import com.leadsubmission.observability.repository.projection.OutcomeCountProjection;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DashboardService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final LeadSubmissionLogRepository logRepository;

    public DashboardService(
            UserRepository userRepository,
            LeadRepository leadRepository,
            LeadSubmissionLogRepository logRepository
    ) {
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.logRepository = logRepository;
    }

    // ============================================================
    //  Dashboard Summary (Overall + Today)
    // ============================================================

    public DashboardSummaryResponse getSummary() {

        // ---------- Overall (All Time) ----------

        long totalUsers = userRepository.count();
        long totalLeads = leadRepository.count();
        long submissionAttempts = logRepository.count();
        long successCount = logRepository.countSuccessfulAttempts();

        double successRate = 0.0;
        if (submissionAttempts > 0) {
            successRate = (double) successCount / submissionAttempts * 100;
        }

        // ---------- Today (IST) ----------

        LocalDate today = LocalDate.now(IST);
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        long submissionAttemptsToday =
                logRepository.countTotalAttempts(startOfToday, startOfTomorrow);

        long successCountToday = 0;
        List<OutcomeCountProjection> todayOutcomes =
                logRepository.countByOutcome(startOfToday, startOfTomorrow);

        for (OutcomeCountProjection row : todayOutcomes) {
            if ("SUCCESS".equals(row.getOutcome())) {
                successCountToday = row.getCount();
                break;
            }
        }

        long totalLeadsToday = successCountToday;

        double successRateToday = 0.0;
        if (submissionAttemptsToday > 0) {
            successRateToday =
                    (double) successCountToday / submissionAttemptsToday * 100;
        }

        // ---------- Response ----------

        return new DashboardSummaryResponse(
                totalUsers,
                totalLeads,
                submissionAttempts,
                successCount,
                successRate,
                totalLeadsToday,
                submissionAttemptsToday,
                successCountToday,
                successRateToday
        );
    }

    // ============================================================
    //  Dashboard Graph: Last 7 Days Success vs Failure
    // ============================================================

    public List<DailySuccessFailureProjection> getDailyTrendsLast7Days() {

        /*
         * We include today and the previous 6 days (IST),
         * so the graph always shows exactly 7 days.
         */
        LocalDate today = LocalDate.now(IST);
        LocalDate startDay = today.minusDays(6);

        LocalDateTime startDateTime = startDay.atStartOfDay();

        return logRepository.fetchDailySuccessFailureTrend(startDateTime);
    }
}
