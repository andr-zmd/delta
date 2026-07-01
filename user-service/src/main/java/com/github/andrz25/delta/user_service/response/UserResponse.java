package com.github.andrz25.delta.user_service.response;

import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        String email,
        LocalDate dateOfBirth,
        String phoneNumber,
        Role role) {

    public UserResponse(User user) {
        this(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getRole());
    }
}
