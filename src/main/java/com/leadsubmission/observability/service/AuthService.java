package com.leadsubmission.observability.service;

import com.leadsubmission.observability.entity.User;
import com.leadsubmission.observability.exception.UnauthorizedException;
import com.leadsubmission.observability.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String password) {

        // 1. User lookup
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("User not found"));

        // 2. Password check (plain text by design)
        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid password");
        }

        // 3. Success
        return user;
    }
}
