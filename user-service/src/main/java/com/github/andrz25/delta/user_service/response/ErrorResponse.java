package com.github.andrz25.delta.user_service.response;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {}
