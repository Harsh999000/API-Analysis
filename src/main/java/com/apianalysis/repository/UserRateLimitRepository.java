package com.apianalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apianalysis.entity.UserRateLimit;

import java.util.Optional;

public interface UserRateLimitRepository extends JpaRepository<UserRateLimit, Long> {
    Optional<UserRateLimit> findByUserId(Long userId);
}
