package com.github.andrz25.delta.user_service.controller;

import com.github.andrz25.delta.user_service.exception.*;
import com.github.andrz25.delta.user_service.response.ErrorResponse;

import org.springframework.security.core.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserExist(UserNotFoundException e) {
        ErrorResponse error =
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException e) {
        ErrorResponse error =
                new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage(), Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailure(AuthenticationException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value() , e.getMessage(), Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
