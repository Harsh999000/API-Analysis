package com.apianalysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.dto.UserLogDto;
import com.apianalysis.entity.User;
import com.apianalysis.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLogService {

    private final UserRepository userRepository;

    public UserLogService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserLogDto> getLatestUsers(int limit) {
        return userRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserLogDto> getAllUsers() {
        return userRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private UserLogDto toDto(User user) {
        return new UserLogDto(
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
