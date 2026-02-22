package com.giggr.identity.domain.identity;

import java.time.LocalDate;
import java.util.Objects;

    public record IdentityProfile(
        String firstName,
        String lastName,
        EntityType entityType,
        Email email,
        String phone,
        DateOfBirth dateOfBirth,
        String country
) {

    public IdentityProfile {
        Objects.requireNonNull(firstName, "First name required");
        Objects.requireNonNull(lastName, "Last name required");
        Objects.requireNonNull(email, "Email required");
        Objects.requireNonNull(entityType, "entity type required");
        Objects.requireNonNull(dateOfBirth, "Date of birth required");

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }


    }
        public void validateEligibility(LocalDate today) {

            if (entityType == EntityType.INDIVIDUAL) {

                if (dateOfBirth.isBelowMinimumAge(today)) {
                    throw new IllegalStateException("Below minimum age");
                }
            }

            if (entityType == EntityType.INDUSTRY
                    || entityType == EntityType.INSTITUTION) {

                if (email.isPublicProvider()) {
                    throw new IllegalArgumentException(
                            "Corporate entities must use corporate email."
                    );
                }
            }
        }
}

