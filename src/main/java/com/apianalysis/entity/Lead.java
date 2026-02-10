package com.apianalysis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_by_user_id", nullable = false)
    private Long userId;

    @Column(name = "lead_name", nullable = false)
    private String name;

    @Column(name = "lead_email", nullable = false)
    private String email;

    @Column(nullable = false)
    private String source;

    @Column(name = "final_page")
    private String finalPage;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
