package com.apianalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.apianalysis.entity.Lead;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    @Query("""
        SELECT l
        FROM Lead l
        ORDER BY l.createdAt DESC
    """)
    List<Lead> findAllOrderByCreatedAtDesc();
}
