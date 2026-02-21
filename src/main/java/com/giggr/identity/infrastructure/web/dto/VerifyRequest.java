package com.giggr.identity.infrastructure.web.dto;
public record VerifyRequest(
        String email,
        String otp
) {}
