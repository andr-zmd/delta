package com.github.andrz25.delta.user_service.exception;

public class DuplicateEmailException extends DuplicateResourceException {
    private final String email;

    public DuplicateEmailException(String email) {
        this.email = email;

        super("User", "email", email);
    }

    public String getEmail() {
        return email;
    }
}
