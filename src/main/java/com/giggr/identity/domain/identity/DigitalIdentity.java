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



    public DigitalIdentity(IdentityProfile profile, Clock clock) {
        this.profile = Objects.requireNonNull(profile, "Profile required");
        this.verification = new Verification(clock);
        // DigitalIdGenerator idGenerator;

        LocalDate today = LocalDate.now(clock);

        int age = profile.dateOfBirth().years(today);

        if (age < 8) {
            throw new IllegalStateException("Below minimum age");
        }

        boolean requiresConsent = age >= 8 && age < 18;
        this.consent = new Consent(requiresConsent, clock);

        this.state = IdentityState.PROSPECT;
    }
    public DigitalIdentity(
            IdentityProfile profile,
            Verification verification,
            Consent consent,
            IdentityState state,
            DigitalId digitalId
    ) {
        this.profile = profile;
        this.verification = verification;
        this.consent = consent;
        this.state = state;
        this.digitalId = digitalId;
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




    public boolean requiresConsent() {
        return consent.isRequired();
    }

    public void provideParentEmail(Email parentEmail) {
        consent.provideParentEmail(parentEmail);
    }

    public void approveConsent() {
        consent.approve();
    }

    public void activate() {

        if (state != IdentityState.VERIFIED) {
            throw new IllegalStateException("Identity must be verified before activation");
        }

        if (digitalId == null) {
            throw new IllegalStateException("Digital ID must be generated before activation");
        }

        if (!consent.isSatisfied()) {
            throw new IllegalStateException("Consent not satisfied");
        }

        this.state = IdentityState.ACTIVE;
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
}

