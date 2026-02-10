package com.leadsubmission.observability.repository;

import com.leadsubmission.observability.entity.UserRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRateLimitRepository extends JpaRepository<UserRateLimit, Long> {
    Optional<UserRateLimit> findByUserId(Long userId);
}
