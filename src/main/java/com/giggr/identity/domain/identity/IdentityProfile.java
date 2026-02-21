package com.giggr.identity.domain.identity;

import java.util.Objects;

    public record IdentityProfile(
        String firstName,
        String lastName,
        Email email,
        String phone,
        DateOfBirth dateOfBirth,
        String country
) {

    public IdentityProfile {
        Objects.requireNonNull(firstName, "First name required");
        Objects.requireNonNull(lastName, "Last name required");
        Objects.requireNonNull(email, "Email required");
        Objects.requireNonNull(dateOfBirth, "Date of birth required");

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }


    }
}

