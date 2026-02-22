//package com.giggr.identity.domain;
//
//import com.giggr.identity.application.IdentityService;
//import com.giggr.identity.domain.identity.*;
//import com.giggr.identity.domain.verification.OtpCode;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class IdentityLifecycleIntegrationTest {
//
//    @Autowired
//    private IdentityService service;
//
//    @Autowired
//    private IdentityRepository repository;
//
//    @Test
//    void adult_full_lifecycle() {
//
//        IdentityProfile adultProfile = new IdentityProfile(
//                "Jane",
//                "Rama",
//                new Email("adult@test.com"),
//                "9999999999",
//                new DateOfBirth(LocalDate.of(1995, 1, 1)),
//                "UK"
//        );
//
//        // Register
//        OtpCode otp = service.register(adultProfile);
//
//        // Verify
//        service.verify(adultProfile.email(), otp);
//
//        // Activate
//        service.activate(adultProfile.email());
//
//        DigitalIdentity identity =
//                repository.findByEmail(adultProfile.email()).orElseThrow();
//
//        assertTrue(identity.isActive());
//        assertNotNull(identity.getDigitalId());
//    }
//
//    @Test
//    void minor_requires_consent_flow() {
//
//        IdentityProfile minorProfile = new IdentityProfile(
//                "John",
//                "Doe",
//                new Email("minor@test.com"),
//                "8888888888",
//                new DateOfBirth(LocalDate.of(2015, 1, 1)),
//                "India"
//        );
//
//        OtpCode otp = service.register(minorProfile);
//
//        service.verify(minorProfile.email(), otp);
//
//        // Activation should fail
//        assertThrows(IllegalStateException.class, () ->
//                service.activate(minorProfile.email())
//        );
//
//        service.provideParentEmail(
//                minorProfile.email(),
//                new Email("parent@test.com")
//        );
//
//        service.approveConsent(minorProfile.email());
//
//        service.activate(minorProfile.email());
//
//        DigitalIdentity identity =
//                repository.findByEmail(minorProfile.email()).orElseThrow();
//
//        assertTrue(identity.isActive());
//        assertNotNull(identity.getDigitalId());
//    }
//}
