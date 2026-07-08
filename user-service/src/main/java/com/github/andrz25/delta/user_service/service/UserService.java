package com.github.andrz25.delta.user_service.service;

import com.github.andrz25.delta.user_service.exception.DuplicateResourceException;
import com.github.andrz25.delta.user_service.exception.ResourceNotFoundException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.request.UserUpdateRequest;
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

        try {
            User saved = userRepository.save(user);

            return new UserResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "An account with this username or email already exists");
        }
    }

    public UserResponse getUserById(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with id: " + id));

        UserResponse response = new UserResponse(user);

        return response;
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("An account with this username already exists");
        }

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        try {
            User saved = userRepository.save(user);

            return new UserResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "An account with this username or email already exists");
        }
    }

    // TODO: Restric deletion to the account owner when Spring Security is added or an admin
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
