package com.giggr.identity.domain;

import com.giggr.identity.application.IdentityService;
import com.giggr.identity.domain.identity.*;
import com.giggr.identity.infrastructure.persistence.InMemoryIdentityRepository;
import com.giggr.identity.domain.verification.OtpCode;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class IdentityLifecycleScenarioTest {

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"),
                ZoneOffset.UTC
        );
    }

    private static class TestContext {
        IdentityService service;
        IdentityRepository repository;

        TestContext(IdentityService service, IdentityRepository repository) {
            this.service = service;
            this.repository = repository;
        }
    }

    private TestContext createContext() {
        Clock clock = fixedClock();

        DigitalIdGenerator generator =
                new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);

        IdentityRepository repository =
                new InMemoryIdentityRepository();

        IdentityService service =
                new IdentityService(clock, generator, repository);

        return new TestContext(service, repository);
    }

    // -----------------------------------------
    // 🧪 ADULT FLOW
    // -----------------------------------------

    @Test
    void adult_full_lifecycle_should_activate_successfully() {

        TestContext ctx = createContext();
        IdentityService service = ctx.service;
        IdentityRepository repository = ctx.repository;

        Email email = new Email("adult@example.com");

        IdentityProfile profile = new IdentityProfile(
                "John",
                "Doe",
                email,
                "9999999999",
                new DateOfBirth(LocalDate.of(1990, 1, 1)), // adult
                "INDIA"
        );

        // Register
        OtpCode otp = service.register(profile);
        assertNotNull(otp);

        // Verify (ID should be assigned here)
        service.verify(email, otp);

        // Activate
        service.activate(email);

        DigitalIdentity identity =
                repository.findByEmail(email).orElseThrow();

        assertEquals(IdentityState.ACTIVE, identity.getState());
        assertNotNull(identity.getDigitalId());
    }

    // -----------------------------------------
    // 🧪 MINOR FLOW
    // -----------------------------------------

    @Test
    void minor_full_lifecycle_requires_consent() {

        TestContext ctx = createContext();
        IdentityService service = ctx.service;
        IdentityRepository repository = ctx.repository;

        Email childEmail = new Email("child@example.com");
        Email parentEmail = new Email("parent@example.com");

        IdentityProfile profile = new IdentityProfile(
                "Jane",
                "Doe",
                childEmail,
                "8888888888",
                new DateOfBirth(LocalDate.of(2015, 1, 1)), // minor
                "INDIA"
        );

        // Register
        OtpCode otp = service.register(profile);
        assertNotNull(otp);

        // Verify (ID should be assigned here)
        service.verify(childEmail, otp);

        // Activation should fail (no consent yet)
        assertThrows(IllegalStateException.class,
                () -> service.activate(childEmail));

        // Parent approves consent
        service.approveConsent(childEmail);

        // Now activation should succeed
        service.activate(childEmail);

        DigitalIdentity identity =
                repository.findByEmail(childEmail).orElseThrow();

        assertEquals(IdentityState.ACTIVE, identity.getState());
        assertNotNull(identity.getDigitalId());
    }

    // -----------------------------------------
    // 🧪 INVALID FLOW
    // -----------------------------------------

    @Test
    void activate_before_verification_should_fail() {

        TestContext ctx = createContext();
        IdentityService service = ctx.service;

        Email email = new Email("invalid@example.com");

        IdentityProfile profile = new IdentityProfile(
                "No",
                "Verify",
                email,
                "7777777777",
                new DateOfBirth(LocalDate.of(1990, 1, 1)),
                "INDIA"
        );

        service.register(profile);

        assertThrows(IllegalStateException.class,
                () -> service.activate(email));
    }
}
