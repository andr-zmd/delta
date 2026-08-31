package com.github.andrz25.delta.user_service.exception;

public class DuplicateUsernameException extends DuplicateResourceException {
    public DuplicateUsernameException(String username) {
        super("User", "username", username);
    }
}
