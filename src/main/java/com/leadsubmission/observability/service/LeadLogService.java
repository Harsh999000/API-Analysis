package com.leadsubmission.observability.service;

import com.leadsubmission.observability.dto.LeadLogDto;
import com.leadsubmission.observability.entity.Lead;
import com.leadsubmission.observability.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadLogService {

    private final LeadRepository leadRepository;

    public LeadLogService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public List<LeadLogDto> getLatestLeads(int limit) {
        return leadRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<LeadLogDto> getAllLeads() {
        return leadRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LeadLogDto toDto(Lead lead) {
        return new LeadLogDto(
                lead.getName(),
                lead.getEmail(),
                lead.getSource(),
                lead.getFinalPage(),
                lead.getCreatedAt()
        );
    }
}
