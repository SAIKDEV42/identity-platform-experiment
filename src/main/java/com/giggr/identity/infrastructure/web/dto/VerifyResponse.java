package com.giggr.identity.infrastructure.web.dto;

public record VerifyResponse(
        String status,
        String digitalId,
        String message
) {}