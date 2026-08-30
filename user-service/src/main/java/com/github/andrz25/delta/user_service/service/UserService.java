package com.github.andrz25.delta.user_service.service;

import com.github.andrz25.delta.user_service.exception.DuplicateEmailException;
import com.github.andrz25.delta.user_service.exception.DuplicateUsernameException;
import com.github.andrz25.delta.user_service.exception.UserNotFoundException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.request.UserUpdateRequest;
import com.github.andrz25.delta.user_service.response.UserResponse;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User.Builder()
                .setFullName(request.fullName())
                .setUsername(request.username())
                .setEmail(request.email())
                .setPassword(passwordEncoder.encode(request.password()))
                .setDateOfBirth(request.dateOfBirth())
                .setPhoneNumber(request.phoneNumber())
                .addRole(Role.USER)
                .build();

        User saved = userRepository.save(user);

        return new UserResponse(saved);
    }

    @Cacheable(value = "users")
    public UserResponse getUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        UserResponse response = new UserResponse(user);

        return response;
    }

    @CachePut(value = "users", key = "#id")
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException(request.email());
        }

        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateUsernameException(request.username());
        }

        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        User saved = userRepository.save(user);

        return new UserResponse(saved);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
}
