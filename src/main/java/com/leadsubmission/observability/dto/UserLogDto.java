package com.leadsubmission.observability.dto;

import java.time.LocalDateTime;

public class UserLogDto {

    private final String email;
    private final LocalDateTime createdAt;

    public UserLogDto(String email, LocalDateTime createdAt) {
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
