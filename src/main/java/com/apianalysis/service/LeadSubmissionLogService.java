package com.apianalysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.dto.LeadSubmissionLogDto;
import com.apianalysis.entity.LeadSubmissionLog;
import com.apianalysis.repository.LeadSubmissionLogRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadSubmissionLogService {

    private final LeadSubmissionLogRepository repository;

    public LeadSubmissionLogService(LeadSubmissionLogRepository repository) {
        this.repository = repository;
    }

    public List<LeadSubmissionLogDto> getLatestSubmissions(int limit) {
        return repository.findAllOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LeadSubmissionLogDto toDto(LeadSubmissionLog log) {
        return new LeadSubmissionLogDto(
                log.getUserEmail(),
                log.getLeadName(),
                log.getLeadEmail(),
                log.getSource(),
                log.getFinalPage(),
                log.getOutcome(),
                log.getFailureReason(),
                log.getCreatedAt()
        );
    }
}
