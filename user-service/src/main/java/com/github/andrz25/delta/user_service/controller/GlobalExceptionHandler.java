package com.github.andrz25.delta.user_service.controller;

import com.github.andrz25.delta.user_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserDoesNotExist(UserNotFoundException e, HttpServletRequest request) {
        String detail = "User " + e.getUserId() + " not found";

        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", detail, request,
                Map.of("userId", e.getUserId()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateUsername(DuplicateUsernameException e,
            HttpServletRequest request) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("resourceType", "user");
        properties.put("fieldName", e.getFieldValue());
        properties.put("fieldValue", e.getFieldValue());

        return createProblemDetail(HttpStatus.CONFLICT, "Username Already Exists", e.getMessage(), request, properties);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("resourceType", "user");
        properties.put("fieldName", e.getFieldName());
        properties.put("fieldValue", e.getFieldValue());

        return createProblemDetail(HttpStatus.CONFLICT, "Email Already Exists", e.getMessage(), request, properties);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateResource(DuplicateResourceException e,
            HttpServletRequest request) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("resourceType", e.getResourceType());
        properties.put("fieldName", e.getFieldName());
        properties.put("fieldValue", e.getFieldValue());

        return createProblemDetail(HttpStatus.CONFLICT, "Resource Already Exists", e.getMessage(), request, properties);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationFailure(AuthenticationException e,
            HttpServletRequest request) {

        return createProblemDetail(HttpStatus.UNAUTHORIZED, "Authentication Failure", e.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidArgument(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing + ", " + replacement));

        return createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failed for the given values",
                request, Map.of("errors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception exception, HttpServletRequest request) {
        String detail = "Server error, try again later!";

        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", detail, request, null);
    }

    public ResponseEntity<ProblemDetail> createProblemDetail(HttpStatusCode status, String title, String detail,
            HttpServletRequest request, Map<String, Object> properties) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);

        pd.setTitle(title);
        pd.setInstance(URI.create(request.getRequestURI()));

        if (properties != null) {
            pd.setProperties(properties);
        }

        pd.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity.status(status).body(pd);
    }
}
