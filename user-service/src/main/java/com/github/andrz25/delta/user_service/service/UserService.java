package com.github.andrz25.delta.user_service.service;

import com.github.andrz25.delta.user_service.exception.DuplicateResourceException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.response.UserResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("An account with this username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user =
                new User.Builder()
                        .setFullName(request.fullName())
                        .setUsername(request.username())
                        .setEmail(request.email())
                        .setDateOfBirth(request.dateOfBirth())
                        .setPhoneNumber(request.phoneNumber())
                        .setRole(Role.USER)
                        .build();

        User saved;

        try {
            saved = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "An account with this username or email already exists");
        }

        return new UserResponse(saved);
    }
}
