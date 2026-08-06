package com.github.andrz25.delta.user_service.request;

import com.github.andrz25.delta.user_service.validation.ValidPhoneNumber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserRegistrationRequest(
        @NotBlank(message = "Full name cannot be blank") @Size(max = 255, message = "Full name must not exceed 255 characters") String fullName,

        @NotBlank(message = "Username cannot be blank") @Pattern(regexp = "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$", message = "Invalid username format") @Size(min = 3, max = 50, message = "Username must be between 3 to 50 characters") String username,

        @NotBlank(message = "Email address cannot be blank") @Email(message = "A valid email address must be provided") @Size(max = 255, message = "Email address must not exceed 255 characters") String email,

        @NotBlank(message = "Password cannot be blank") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password,

        @NotNull(message = "Date of birth not be null") @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,

        @NotBlank(message = "Phone number must not be null or blank") @ValidPhoneNumber(message = "Must be a valid phone number") String phoneNumber) {
}
