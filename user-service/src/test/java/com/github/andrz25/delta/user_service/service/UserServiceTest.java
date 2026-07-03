package com.github.andrz25.delta.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.andrz25.delta.user_service.exception.DuplicateResourceException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.response.UserResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    // registerUser()
    @Test
    void givenValidUserRegistrationRequest_whenRegisterUser_thenReturnUserResponse() {
        // Arrange
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        "John Doe",
                        "jnde25",
                        "johndoe@xyz.com",
                        LocalDate.of(2000, 7, 1),
                        "12345678910");

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = userService.registerUser(request);

        // Assert
        assertEquals(request.fullName(), response.fullName());
        assertEquals(request.username(), response.username());
        assertEquals(request.email(), response.email());
        assertEquals(request.dateOfBirth(), response.dateOfBirth());
        assertEquals(request.phoneNumber(), response.phoneNumber());
        assertEquals(Role.USER, response.role());
    }

    @Test
    void givenDuplicateEmail_whenRegisterUser_thenThrowDuplicateResourceException() {
        // Arrange
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        "Jane Doe",
                        "jnde05",
                        "johndoe@xyz.com",
                        LocalDate.of(2005, 7, 1),
                        "12345678910");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> {
                    userService.registerUser(request);
                });
    }

    @Test
    void givenDuplicateUsername_whenRegisterUser_thenThrowDuplicateResourceException() {
        // Arrange
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        "Jane Doe",
                        "jnde05",
                        "janedoe@xyz.com",
                        LocalDate.of(2005, 7, 1),
                        "12345678910");

        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> {
                    userService.registerUser(request);
                });
    }
}
