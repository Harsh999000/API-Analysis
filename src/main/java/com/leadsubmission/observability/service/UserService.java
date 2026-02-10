package com.leadsubmission.observability.service;

import com.leadsubmission.observability.entity.User;
import com.leadsubmission.observability.entity.UserRateLimit;
import com.leadsubmission.observability.exception.UserAlreadyExistsException;
import com.leadsubmission.observability.exception.UserNotFoundException;
import com.leadsubmission.observability.repository.UserRateLimitRepository;
import com.leadsubmission.observability.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRateLimitRepository rateLimitRepository;

    public UserService(
            UserRepository userRepository,
            UserRateLimitRepository rateLimitRepository
    ) {
        this.userRepository = userRepository;
        this.rateLimitRepository = rateLimitRepository;
    }

    /**
     * Create a new user and initialize rate limits
     */
    public void createUser(String email, String password) {

        userRepository.findByEmail(email)
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException(email);
                });

        User user = new User(email, password);
        userRepository.save(user);

        UserRateLimit rateLimit = UserRateLimit.createDefault(user.getId());
        rateLimitRepository.save(rateLimit);
    }

    /**
     * Delete user and associated rate-limit row
     */
    @Transactional
    public void deleteUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        rateLimitRepository.deleteById(user.getId());
        userRepository.delete(user);
    }
}
