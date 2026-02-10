package com.leadsubmission.observability.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LeadRequest {

    @NotBlank(message = "Lead name is required")
    private String name;

    @Email(message = "Invalid lead email")
    @NotBlank(message = "Lead email is required")
    private String email;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Final page is required")
    private String finalPage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFinalPage() {
        return finalPage;
    }

    public void setFinalPage(String finalPage) {
        this.finalPage = finalPage;
    }
}
