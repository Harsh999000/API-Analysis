package com.apianalysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.entity.User;
import com.apianalysis.exception.UnauthorizedException;
import com.apianalysis.repository.UserRepository;

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
