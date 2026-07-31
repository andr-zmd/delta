package com.github.andrz25.delta.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.andrz25.delta.user_service.exception.DuplicateResourceException;
import com.github.andrz25.delta.user_service.exception.UserNotFoundException;
import com.github.andrz25.delta.user_service.model.Role;
import com.github.andrz25.delta.user_service.model.User;
import com.github.andrz25.delta.user_service.repository.UserRepository;
import com.github.andrz25.delta.user_service.request.UserRegistrationRequest;
import com.github.andrz25.delta.user_service.request.UserUpdateRequest;
import com.github.andrz25.delta.user_service.response.UserResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

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

        verify(userRepository, never()).save(any(User.class));
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

        verify(userRepository, never()).save(any(User.class));
    }

    // getUserById()
    @Test
    void givenValidId_whenGetUserById_thenReturnUserResponse() {
        // Arrange
        Long id = 1L;

        User user =
                new User.Builder()
                        .setId(id)
                        .setFullName("John Doe")
                        .setUsername("jnde05")
                        .setEmail("johndoe@xyz.com")
                        .setDateOfBirth(LocalDate.of(2005, 7, 1))
                        .setPhoneNumber("12345678910")
                        .setRole(Role.USER)
                        .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserById(id);

        // Assert
        assertEquals(id, response.id());
        assertEquals(user.getFullName(), response.fullName());
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getDateOfBirth(), response.dateOfBirth());
        assertEquals(user.getPhoneNumber(), response.phoneNumber());
        assertEquals(user.getRole(), response.role());

        verify(userRepository).findById(id);
    }

    @Test
    void givenNonExistentId_whenGetUserById_thenThrowResourceNotFoundException() {
        // Arrange
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository).findById(id);
    }

    // updateUser()
    @Test
    void givenValidUserUpdateRequest_whenUpdateUser_thenReturnUserResponse() {
        // Arrange
        Long id = 1L;
        UserUpdateRequest request =
                new UserUpdateRequest("Jake Doe", "jkde05", "jakedoe@xyz.com", "11987654321");

        User existingUser =
                new User.Builder()
                        .setId(id)
                        .setFullName("John Doe")
                        .setUsername("jnde05")
                        .setEmail("johndoe@xyz.com")
                        .setDateOfBirth(LocalDate.of(2005, 7, 1))
                        .setPhoneNumber("12345678910")
                        .setRole(Role.USER)
                        .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(request.email(), id)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot(request.username(), id)).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = userService.updateUser(id, request);

        // Assert
        assertEquals(id, response.id());
        assertEquals(request.fullName(), response.fullName());
        assertEquals(request.username(), response.username());
        assertEquals(request.email(), response.email());
        assertEquals(existingUser.getDateOfBirth(), response.dateOfBirth());
        assertEquals(request.phoneNumber(), response.phoneNumber());
        assertEquals(existingUser.getRole(), response.role());

        verify(userRepository).existsByEmailAndIdNot(request.email(), id);
        verify(userRepository).existsByUsernameAndIdNot(request.username(), id);
    }

    @Test
    void givenDuplicateEmail_whenUpdateUser_thenThrowDuplicateResourceException() {
        // Arrange
        Long id = 1L;
        UserUpdateRequest request =
                new UserUpdateRequest("John Doe", "jnde05", "johndoe05@xyz.com", "12345678910");

        User existingUser =
                new User.Builder()
                        .setId(id)
                        .setFullName("John Doe")
                        .setUsername("jnde05")
                        .setEmail("johndoe@xyz.com")
                        .setDateOfBirth(LocalDate.of(2005, 7, 1))
                        .setPhoneNumber("12345678910")
                        .setRole(Role.USER)
                        .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(request.email(), id)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(id, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void givenDuplicateUsername_whenUpdateUser_thenThrowDuplicateResourceException() {
        // Arrange
        Long id = 1L;
        UserUpdateRequest request =
                new UserUpdateRequest("John Doe", "jnde2005", "johndoe@xyz.com", "12345678910");

        User existingUser =
                new User.Builder()
                        .setId(id)
                        .setFullName("John Doe")
                        .setUsername("jnde05")
                        .setEmail("johndoe@xyz.com")
                        .setDateOfBirth(LocalDate.of(2005, 7, 1))
                        .setPhoneNumber("12345678910")
                        .setRole(Role.USER)
                        .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(request.email(), id)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot(request.username(), id)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(id, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void givenValidId_whenDeleteUser_thenUserIsDeleted() {
        // Arrange
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        // Act
        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void givenNonExistentUser_whenDeleteUser_thenThrowResourceNotFoundException() {
        // Arrange
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        // Act
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

        verify(userRepository, never()).deleteById(id);
    }
}
