package com.github.andrz25.delta.user_service.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String field, String value) {
        super(String.format("%s '%s' is already in use", field, value));
    }
}
