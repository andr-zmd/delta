package com.github.andrz25.delta.user_service.service;

import com.github.andrz25.delta.user_service.exception.DuplicateResourceException;
import com.github.andrz25.delta.user_service.exception.UserNotFoundException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.request.UserUpdateRequest;
import com.github.andrz25.delta.user_service.response.UserResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

// TODO: Use Redis for caching
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username", request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email", request.email());
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

        try {
            User saved = userRepository.save(user);

            return new UserResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username", request.username());
        }
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with id: " + id));

        UserResponse response = new UserResponse(user);

        return response;
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email", request.email());
        }

        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username", request.username());
        }

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        try {
            User saved = userRepository.save(user);

            return new UserResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username", request.username());
        }
    }

    // TODO: Restrict deletion to the account owner when Spring Security is added or
    // an admin
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
