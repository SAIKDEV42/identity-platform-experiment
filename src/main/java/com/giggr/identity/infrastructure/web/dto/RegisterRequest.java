package com.giggr.identity.infrastructure.web.dto;

public record RegisterRequest(
        String firstName,
        String lastName,
        String organizationName,
        String entityType,
        String email,
        String phone,
        String dateOfBirth,
        String country
    ) {}

