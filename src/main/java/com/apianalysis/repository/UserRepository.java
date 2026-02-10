package com.apianalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.apianalysis.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u
        FROM User u
        ORDER BY u.createdAt DESC
    """)
    List<User> findAllOrderByCreatedAtDesc();
}
