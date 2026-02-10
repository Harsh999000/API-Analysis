package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.entity.User;
import com.leadsubmission.observability.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth-check")
    public String testAuth(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Password") String password
    ) {
        User user = authService.authenticate(email, password);
        return "Authenticated as user ID: " + user.getId();
    }
}
