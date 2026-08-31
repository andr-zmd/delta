package com.github.andrz25.delta.user_service.controller;

import com.github.andrz25.delta.user_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

        Map<String, Object> properties = new HashMap<>();

        properties.put("userId", e.getUserId());

        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", detail, request, properties);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateResource(DuplicateResourceException e,
            HttpServletRequest request) {
        Map<String, Object> properties = new HashMap<>();

        String resourceType = e.getResourceType();
        String fieldName = e.getFieldName();
        String fieldValue = e.getFieldValue();

        String detail = String.format(
                "%s with %s '%s' already exists",
                resourceType, fieldName, fieldValue);

        properties.put("resourceType", resourceType);
        properties.put("fieldName", fieldName);
        properties.put("fieldValue", fieldValue);

        return createProblemDetail(HttpStatus.CONFLICT, "Resource Already Exists", detail, request, properties);
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

        Map<String, Object> properties = new HashMap<>();

        properties.put("errors", errors);

        return createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failed for the given values",
                request, properties);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataViolation(DataIntegrityViolationException e,
            HttpServletRequest request) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Data Integrity Error",
                "The request violates the constraints of the data",
                request,
                null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJson(HttpMessageNotReadableException e,
            HttpServletRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Malformed JSON",
                "Request is invalid, ensure proper request format", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception e, HttpServletRequest request) {
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
