package com.giggr.identity.infrastructure.web.dto;

public record ApproveConsentResponse(
        String status,
        String digitalId,
        String message
) {}
