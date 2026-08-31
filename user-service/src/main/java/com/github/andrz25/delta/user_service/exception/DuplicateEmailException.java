package com.github.andrz25.delta.user_service.exception;

public class DuplicateEmailException extends DuplicateResourceException {
    public DuplicateEmailException(String email) {
        super("User", "email", email);
    }
}
