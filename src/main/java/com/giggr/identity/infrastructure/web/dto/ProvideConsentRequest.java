package com.giggr.identity.infrastructure.web.dto;

public record ProvideConsentRequest(
        String childEmail,
        String parentEmail
) {}
