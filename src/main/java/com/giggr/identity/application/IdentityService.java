package com.giggr.identity.application;


import com.giggr.identity.application.port.EmailSender;
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

    private final EmailSender emailSender;


    public IdentityService(Clock clock, DigitalIdGenerator idGenerator, IdentityRepository repository, EmailSender emailSender) {
        this.clock = clock;
        this.idGenerator = idGenerator;
        this.repository = repository;
        this.emailSender = emailSender;
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


        emailSender.sendOtp(
                identity.getProfile().email().value(),
                otp.value()
        );

        return otp;
    }


//    public String verify(Email email, OtpCode otp) {
//
//        DigitalIdentity identity =
//                repository.findByEmail(email)
//                        .orElseThrow(() -> new IllegalStateException("Identity not found"));
//
//        identity.verifyOtp(otp);
//
//        // adults =Aassign id immediately
//        if (!identity.requiresConsent() && identity.canAssignId()) {
//
//            DigitalId id = idGenerator.generate(identity.getProfile().entityType());
//            identity.assignId(id);
//
//
//            tryActivate(identity);
//        }
//        repository.save(identity);
//
//        if (identity.getDigitalId() != null) {
//            emailSender.sendDigitalId(
//                    identity.getProfile().email().value(),
//                    identity.getDigitalId().value()
//            );
//        }
//
//
//        return identity.getDigitalId() != null
//                ? identity.getDigitalId().value()
//                : "requires Consent";
//
//    }


    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class})
public String verify(Email email, OtpCode inputOtp) {
    DigitalIdentity identity = repository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Identity not found"));

    try {
        identity.verifyOtp(inputOtp);

        if (!identity.requiresConsent() && identity.canAssignId()) {
            DigitalId id = idGenerator.generate(identity.getProfile().entityType());
            identity.assignId(id);
            tryActivate(identity);
        }

        repository.save(identity);

    } catch (IllegalArgumentException | IllegalStateException e) {
        repository.save(identity);
        throw e;
    }

    if (identity.getDigitalId() != null) {
        emailSender.sendDigitalId(
                identity.getProfile().email().value(),
                identity.getDigitalId().value()
        );
    }

    return identity.getDigitalId() != null
            ? identity.getDigitalId().value()
            : "requires Consent";
}

    public String approveConsent(Email childEmail) {
        DigitalIdentity identity = repository.findByEmail(childEmail)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.approveConsent();
        if (identity.canAssignId()) {
            DigitalId id = idGenerator.generate(identity.getProfile().entityType());
            identity.assignId(id);


            tryActivate(identity);
        }

        repository.save(identity);

        emailSender.sendConsentRequest(
                identity.getConsent().getParentEmail().value(),
                identity.getProfile().email().value()
        );

        return identity.getDigitalId() != null ?
                identity.getDigitalId().value() :
                "PENDING_ID_ASSIGNMENT";
    }


    public void provideParentEmail(Email childEmail, Email parentEmail) {
        DigitalIdentity identity = repository.findByEmail(childEmail)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.provideParentEmail(parentEmail);

        repository.save(identity);
    }

    public void acceptTerms(Email email) {

        DigitalIdentity identity = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Identity not found"));

        identity.acceptTerms();

        // Iff everything ready-> activate
        tryActivate(identity);


        repository.save(identity);
    }


    private void tryActivate(DigitalIdentity identity) {
        if (identity.canActivate()) {
            identity.activate();
        }
    }


}

