package com.giggr.identity.domain.identity;

import java.time.LocalDate;
import java.util.Objects;

    public record IdentityProfile(
        String firstName,
        String lastName,
        String organizationName,
        EntityType entityType,
        Email email,
        String phone,
        DateOfBirth dateOfBirth,
        String country
) {

        public IdentityProfile {

             Objects.requireNonNull(entityType, "Entity type required");
            Objects.requireNonNull(email, "Email required");

            Objects.requireNonNull(country, "Country required");

             if (entityType == EntityType.INDIVIDUAL) {
                Objects.requireNonNull(phone, "Phone required");
                Objects.requireNonNull(firstName, "First name required for individual");
                Objects.requireNonNull(lastName, "Last name required for individual");

                // Note: organizationName remains whatever was passed (usually null)
            } else {
                Objects.requireNonNull(organizationName, "Organization name required for corporate entity");
             }
        }


        public void validateEligibility(LocalDate today) {

            if (entityType == EntityType.INDIVIDUAL) {
                if (dateOfBirth == null) {
                    throw new IllegalStateException("Date of birth required");
                }
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

