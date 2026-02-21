package com.giggr.identity.infrastructure.web.dto;

     public record RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String phone,
            String dob,
            String country
    ) {}

