package com.github.andrz25.delta.user_service.exception;

public class DuplicateUsernameException extends DuplicateResourceException {

    private final String username;

    public DuplicateUsernameException(String username) {
        this.username = username;

        super("User", "username", username);
    }

    public String getUsername() {
        return username;
    }
}
