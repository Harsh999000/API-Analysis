package com.apianalysis.dto;

import java.time.LocalDateTime;

public class LeadLogDto {

    private final String leadName;
    private final String leadEmail;
    private final String source;
    private final String finalPage;
    private final LocalDateTime createdAt;

    public LeadLogDto(
            String leadName,
            String leadEmail,
            String source,
            String finalPage,
            LocalDateTime createdAt
    ) {
        this.leadName = leadName;
        this.leadEmail = leadEmail;
        this.source = source;
        this.finalPage = finalPage;
        this.createdAt = createdAt;
    }

    public String getLeadName() {
        return leadName;
    }

    public String getLeadEmail() {
        return leadEmail;
    }

    public String getSource() {
        return source;
    }

    public String getFinalPage() {
        return finalPage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
