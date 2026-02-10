package com.apianalysis.dto;

public class LeadResponse {

    private String outcome;
    private String message;

    public LeadResponse(String outcome, String message) {
        this.outcome = outcome;
        this.message = message;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getMessage() {
        return message;
    }
}