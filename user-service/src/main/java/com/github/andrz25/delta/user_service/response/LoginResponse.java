package com.github.andrz25.delta.user_service.response;

public record LoginResponse(String token, String tokenType) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}
