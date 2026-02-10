package com.leadsubmission.observability.service;

import com.leadsubmission.observability.entity.PageVisitLog;
import com.leadsubmission.observability.repository.PageVisitLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PageVisitLogService {

    private final PageVisitLogRepository repository;

    public PageVisitLogService(PageVisitLogRepository repository) {
        this.repository = repository;
    }

    public void logVisit(String ipAddress, String pagePath) {

        System.out.println("LOGGING VISIT TO DB");

        
        PageVisitLog log = new PageVisitLog();
        log.setIpAddress(ipAddress);
        log.setPagePath(pagePath);
        log.setVisitedAt(LocalDateTime.now());

        repository.save(log);
    }
}
