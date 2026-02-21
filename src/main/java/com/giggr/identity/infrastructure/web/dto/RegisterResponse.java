package com.giggr.identity.infrastructure.web.dto;

public record RegisterResponse(
        String status,
        String email,
        String message,
        String otp   // remove later in production
) {}
