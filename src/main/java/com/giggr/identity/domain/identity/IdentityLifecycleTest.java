package com.giggr.identity.domain.identity;


import com.giggr.identity.application.IdentityService;
import com.giggr.identity.domain.verification.OtpCode;
import com.giggr.identity.infrastructure.persistence.InMemoryIdentityRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class IdentityLifecycleTest {

    public static void main(String[] args) {

        Clock clock = Clock.fixed(
                Instant.parse("2026-01-10T10:00:00Z"),
                ZoneOffset.UTC
        );

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        InMemoryIdentityRepository repository =
                new InMemoryIdentityRepository();

        IdentityService service =
                new IdentityService(clock, generator, repository);

        System.out.println("=== ADULT FLOW TEST ===");

        IdentityProfile adultProfile = new IdentityProfile("Jane",
                "rama",
                new Email("adult@test.com"),"5685684565",
                new DateOfBirth(LocalDate.of(1995, 1, 1)),
                "England"
        );

        OtpCode adultOtp = service.register(adultProfile);
        service.verify(adultProfile.email(), adultOtp);
        service.activate(adultProfile.email());

        DigitalIdentity adultIdentity =
                repository.findByEmail(adultProfile.email()).orElseThrow();

        assert adultIdentity.isActive();
        assert adultIdentity.getDigitalId() != null;

        System.out.println("Adult flow passed ✅");


        System.out.println("\n=== MINOR FLOW TEST ===");

        IdentityProfile minorProfile = new IdentityProfile( "John",
                "Doe",
                new Email("minor@test.com"),"5658568545",
                new DateOfBirth(LocalDate.of(2015, 1, 1)),
                "india"

        );

        OtpCode minorOtp = service.register(minorProfile);
        service.verify(minorProfile.email(), minorOtp);

        try {
            service.activate(minorProfile.email());
            throw new RuntimeException("Activation should have failed for minor");
        } catch (IllegalStateException e) {
            System.out.println("Minor activation blocked as expected ✅");
        }

        service.provideParentEmail(
                minorProfile.email(),
                new Email("parent@test.com")
        );

        service.approveConsent(minorProfile.email());

        service.activate(minorProfile.email());

        DigitalIdentity minorIdentity =
                repository.findByEmail(minorProfile.email()).orElseThrow();

        assert minorIdentity.isActive();
        assert minorIdentity.getDigitalId() != null;

        System.out.println("Minor flow passed ✅");


        System.out.println("\n=== DUPLICATE REGISTRATION TEST ===");

        try {
            service.register(adultProfile);
            throw new RuntimeException("Duplicate registration should fail");
        } catch (IllegalStateException e) {
            System.out.println("Duplicate registration blocked as expected ✅");
        }


        System.out.println("Duplicate registration handled correctly ✅");


        System.out.println("\n=== IDEMPOTENT VERIFY TEST ===");

        IdentityProfile idempotentProfile = new IdentityProfile(
                "Test",
                "User",
                new Email("idempotent@test.com"),
                "9999999999",
                new DateOfBirth(LocalDate.of(1990, 1, 1)),
                "India"
        );

        OtpCode otp = service.register(idempotentProfile);

// First verify
        service.verify(idempotentProfile.email(), otp);

// Second verify (should not throw, should not regenerate ID)
        service.verify(idempotentProfile.email(), otp);

        DigitalIdentity identity =
                repository.findByEmail(idempotentProfile.email()).orElseThrow();

        assert identity.getDigitalId() != null;
        assert identity.getState() == IdentityState.VERIFIED;

        System.out.println("Idempotent verify passed ✅");

    }
}
