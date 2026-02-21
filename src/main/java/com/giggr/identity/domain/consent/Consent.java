package com.giggr.identity.domain.consent;


import com.giggr.identity.domain.identity.Email;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class Consent {

    private final Clock clock;
    private final boolean required;

    private ConsentState state;
    private Instant approvedAt;
    Email parentEmail; // nullable if adult


    public Consent(boolean required, Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        this.required = required;

        if (required) {
            this.state = ConsentState.PENDING;
        } else {
            this.state = ConsentState.NOT_REQUIRED;
        }
    }

// only for relode purpopse
    public Consent(
            boolean required,
            ConsentState state,
            String parentEmail,
            Instant approvedAt,
            Clock clock
    ) {
        this.state = state;
        this.parentEmail = parentEmail != null ? new Email(parentEmail) : null;
        this.approvedAt = approvedAt;
        this.clock = clock;
        this.required=required;
    }


    public void provideParentEmail(Email parentEmail) {
        if (!required) {
            throw new IllegalStateException("Consent not required");
        }
        this.parentEmail = parentEmail;
    }

    public void approve() {
        if (state != ConsentState.PENDING) {
            throw new IllegalStateException("Consent not pending");
        }

        this.state = ConsentState.APPROVED;
        this.approvedAt = Instant.now(clock);
    }

    public boolean isSatisfied() {
        return state == ConsentState.NOT_REQUIRED
                || state == ConsentState.APPROVED;
    }


    public boolean isRequired() {
        return required;

    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public ConsentState getState() {
        return state;
    }

    public Email getParentEmail() {
        return parentEmail;
    }
}

