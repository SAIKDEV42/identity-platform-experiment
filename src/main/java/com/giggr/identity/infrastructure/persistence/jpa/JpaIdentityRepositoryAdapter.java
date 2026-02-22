package com.giggr.identity.infrastructure.persistence.jpa;

import com.giggr.identity.domain.consent.Consent;
import com.giggr.identity.domain.identity.*;
import com.giggr.identity.domain.verification.Verification;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JpaIdentityRepositoryAdapter implements IdentityRepository {
    private final Clock clock;

    private final SpringDataIdentityRepository springRepository;

    public JpaIdentityRepositoryAdapter(Clock clock, SpringDataIdentityRepository springRepository) {
        this.clock = clock;
        this.springRepository = springRepository;
    }

    @Override
    public void save(DigitalIdentity identity) {

        JpaDigitalIdentityEntity entity =
                springRepository.findByEmail(identity.getProfile().email().value())
                        .orElseGet(JpaDigitalIdentityEntity::new);

        updateEntityFields(entity, identity);

        springRepository.save(entity);
    }


    @Override
    public Optional<DigitalIdentity> findByEmail(Email email) {
        return springRepository.findByEmail(email.value())
                .map(this::toDomain);
    }


    @Override
    public Optional<DigitalIdentity> findByDigitalId(DigitalId digitalId) {

        Objects.requireNonNull(digitalId, "DigitalId required");

        return springRepository
                .findByDigitalId(digitalId.value())
                .map(this::toDomain);
    }


    private void updateEntityFields(
            JpaDigitalIdentityEntity entity,
            DigitalIdentity domain
    ) {
        entity.setEmail(domain.getProfile().email().value());
        entity.setFirstName(domain.getProfile().firstName());
        entity.setLastName(domain.getProfile().lastName());
        entity.setDateOfBirth(domain.getProfile().dateOfBirth().value());
        entity.setTermsAccepted(domain.isTermsAccepted());
        entity.setEntityType(domain.getProfile().entityType());

        entity.setState(domain.getState());
        entity.setDigitalId(
                domain.getDigitalId() != null
                        ? domain.getDigitalId().value()
                        : null
        );

        // Verification
        entity.setVerificationState(
                domain.getVerification().getState()
        );

        entity.setOtpCode(
                domain.getVerification().getOtpCode() != null
                        ? domain.getVerification().getOtpCode().value()
                        : null
        );

        entity.setOtpExpiry(domain.getVerification().getOtpExpiry());
        entity.setOtpAttempts(domain.getVerification().getAttempts());

        // Consent
        entity.setConsentState(
                domain.getConsent().getState()
        );
        entity.setConsentRequired(domain.getConsent().isRequired());

        entity.setParentEmail(
                domain.getConsent().getParentEmail() != null
                        ? domain.getConsent().getParentEmail().value()
                        : null
        );

        entity.setConsentApprovedAt(domain.getConsent().getApprovedAt());
    }





    // mapping methods

    private DigitalIdentity toDomain(JpaDigitalIdentityEntity entity) {

        IdentityProfile profile = new IdentityProfile(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEntityType(),
                new Email(entity.getEmail()),
                entity.getPhone(),
                new DateOfBirth(entity.getDateOfBirth()),
                entity.getCountry()
        );

        Verification verification = new Verification(
                entity.getVerificationState(),
                entity.getOtpCode(),
                entity.getOtpExpiry(),
                entity.getOtpAttempts(),
                entity.getLastSeenAt(),
                clock
        );

        Consent consent = new Consent(
                entity.isConsentRequired(),
                entity.getConsentState(),
                entity.getParentEmail(),
                entity.getConsentApprovedAt(),
                clock
        );

        DigitalId digitalId = entity.getDigitalId() != null
                ? new DigitalId(entity.getDigitalId())
                : null;

        return new DigitalIdentity(
                profile,
                verification,
                consent,
                entity.getState(),
                digitalId,entity.isTermsAccepted()

        );
    }

}

