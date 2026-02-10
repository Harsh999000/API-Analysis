package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.dto.UserLogDto;
import com.leadsubmission.observability.service.UserLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs/users")
public class UserLogsController {

    private final UserLogService userLogService;

    public UserLogsController(UserLogService userLogService) {
        this.userLogService = userLogService;
    }

    @GetMapping
    public List<UserLogDto> getLatestUsers(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return userLogService.getLatestUsers(limit);
    }

    @GetMapping("/export")
    public List<UserLogDto> exportAllUsers() {
        return userLogService.getAllUsers();
    }
}
