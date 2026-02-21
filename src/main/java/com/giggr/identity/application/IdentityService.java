package com.giggr.identity.application;


import com.giggr.identity.domain.identity.*;
import com.giggr.identity.domain.verification.OtpCode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class IdentityService {

    private final Clock clock;

    private final DigitalIdGenerator idGenerator;

    private final IdentityRepository repository;


    public IdentityService(Clock clock, DigitalIdGenerator idGenerator, IdentityRepository repository) {
        this.clock = clock;
        this.idGenerator = idGenerator;
        this.repository = repository;
    }

    public OtpCode register(IdentityProfile profile) {

        Objects.requireNonNull(profile, "Profile required");


        Optional<DigitalIdentity> existing =
                repository.findByEmail(profile.email());

        if (existing.isPresent()) {

            DigitalIdentity identity = existing.get();

            if (identity.getState() == IdentityState.PROSPECT) {
                OtpCode otp = identity.sendOtp();
                repository.save(identity);
                return otp;
            }

            if (identity.getState() == IdentityState.VERIFIED ||
                    identity.getState() == IdentityState.ACTIVE) {

                throw new IllegalStateException("Identity already exists");
            }

            if (identity.getState() == IdentityState.SUSPENDED) {
                throw new IllegalStateException("Identity suspended");
            }
        }

        DigitalIdentity identity = new DigitalIdentity(profile, clock);
        OtpCode otp = identity.sendOtp();
        repository.save(identity);
        return otp;
    }


    public String verify(Email email, OtpCode otp) {

        DigitalIdentity identity =
                repository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("Identity not found"));
        identity.verifyOtp(otp);

        if (!identity.requiresConsent() && identity.canAssignId()) {

            DigitalId id = idGenerator.generate();
            identity.assignId(id);
        }
        repository.save(identity);


        return identity.getDigitalId() != null
                ? identity.getDigitalId().value()
                : null;

    }

    public void activate(Email email) {
        DigitalIdentity identity = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.activate();
        repository.save(identity);
    }

    public String approveConsent(Email childEmail) {
        DigitalIdentity identity = repository.findByEmail(childEmail)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.approveConsent();
        if (identity.canAssignId()) {
            DigitalId id = idGenerator.generate();
            identity.assignId(id);
        }

        repository.save(identity);

        return identity.getDigitalId().value();
    }


    public void provideParentEmail(Email childEmail, Email parentEmail) {
        DigitalIdentity identity = repository.findByEmail(childEmail)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.provideParentEmail(parentEmail);

        repository.save(identity);
    }


}

