package com.apianalysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.dto.LeadLogDto;
import com.apianalysis.entity.Lead;
import com.apianalysis.repository.LeadRepository;

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
