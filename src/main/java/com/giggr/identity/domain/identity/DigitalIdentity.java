package com.giggr.identity.domain.identity;


import com.giggr.identity.domain.consent.Consent;
import com.giggr.identity.domain.verification.OtpCode;
import com.giggr.identity.domain.verification.Verification;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class DigitalIdentity {

    private final Consent consent;
    private DigitalId digitalId;
    private IdentityState state;
    private final IdentityProfile profile;
    private final Verification verification;

    private boolean termsAccepted;

    public DigitalIdentity(IdentityProfile profile, Clock clock) {
        this.profile = Objects.requireNonNull(profile, "Profile required");

        LocalDate today = LocalDate.now(clock);

        profile.validateEligibility(today);

        boolean requiresConsent = profile.entityType() == EntityType.INDIVIDUAL
                && profile.dateOfBirth().isMinor(today);
        this.consent = new Consent(requiresConsent, clock);

        if (!this.requiresConsent() && profile.email().isPublicProvider()) {
            throw new IllegalArgumentException("Adults must provide a valid company email address.");
        }

        this.verification = new Verification(clock);
        // DigitalIdGenerator idGenerator;

        this.state = IdentityState.PROSPECT;
        this.termsAccepted = false;

    }

    public DigitalIdentity(
            IdentityProfile profile,
            Verification verification,
            Consent consent,
            IdentityState state,
            DigitalId digitalId,
            boolean termsAccepted


    ) {
        this.profile = profile;
        this.verification = verification;
        this.consent = consent;
        this.state = state;
        this.digitalId = digitalId;
        this.termsAccepted = termsAccepted;
    }


    public OtpCode sendOtp() {
        if (state != IdentityState.PROSPECT) {
            throw new IllegalStateException("OTP can only be sent for prospects");
        }
        return verification.sendOtp();
    }

    public void verifyOtp(OtpCode code) {
        if (state == IdentityState.VERIFIED) {
            return; // idempotent
        }
        if (state != IdentityState.PROSPECT) {
            throw new IllegalStateException("Verification not allowed in current state");
        }

        verification.verify(code);

        if (verification.isVerified()) {
            this.state = IdentityState.VERIFIED;
        }
    }


    public void assignId(DigitalId id) {
        Objects.requireNonNull(id, "DigitalId required");

        if (state != IdentityState.VERIFIED) {
            throw new IllegalStateException("Cannot generate ID before verification");
        }
        if (this.digitalId != null) {
            return;
        }
        this.digitalId = id;
    }

    public boolean canAssignId() {
        return state == IdentityState.VERIFIED && digitalId == null;
    }

    public boolean canActivate() {
        return state == IdentityState.VERIFIED
                && digitalId != null
                && consent.isSatisfied()
                && termsAccepted;
    }


    public boolean requiresConsent() {
        return consent.isRequired();
    }

    public void provideParentEmail(Email parentEmail) {
        consent.provideParentEmail(parentEmail);
    }

    public void approveConsent() {
        consent.approve();
    }


//    public boolean readyForActivation() {
//        return state == IdentityState.VERIFIED
//                && digitalId != null
//                && consent.isSatisfied()
//                && termsAccepted;
//    }

    public void activate() {

        if (state == IdentityState.ACTIVE) return;

        if (state != IdentityState.VERIFIED) {
            throw new IllegalStateException("Identity must be verified before activation");
        }

        if (digitalId == null) {
            throw new IllegalStateException("Digital ID must be generated before activation");
        }

        if (!consent.isSatisfied()) {
            throw new IllegalStateException("Consent not satisfied");
        }

        if (!termsAccepted) {
            throw new IllegalStateException("Terms not accepted");
        }

        this.state = IdentityState.ACTIVE;
    }

    public void acceptTerms() {
        if (digitalId == null) {
            throw new IllegalStateException("Assign ID before accepting terms");
        }

        this.termsAccepted = true;
//        this.termsAcceptedAt = Instant.now(clock);
    }

    public IdentityState getState() {
        return state;
    }

    public DigitalId getDigitalId() {
        return digitalId;
    }

    public IdentityProfile getProfile() {
        return profile;
    }

    public boolean isVerified() {
        return state == IdentityState.VERIFIED;
    }

    public boolean isActive() {
        return state == IdentityState.ACTIVE;
    }


    public Consent getConsent() {
        return consent;
    }

    public Verification getVerification() {
        return verification;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }
}

