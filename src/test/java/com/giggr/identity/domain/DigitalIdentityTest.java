//package com.giggr.identity.domain;
//
//import com.giggr.identity.domain.identity.DigitalIdentity;
//import com.giggr.identity.domain.identity.IdentityState;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//public class DigitalIdentityTest {
//
//    @Test
//    void shouldRejectInvalidDigitalIdLength() {
//        LocalDate dob = LocalDate.of(2000, 1, 1);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                new DigitalIdentity("12345", dob)
//        );
//    }
//
//    @Test
//    void shouldRejectNonNumericDigitalId() {
//        LocalDate dob = LocalDate.of(2000, 1, 1);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                new DigitalIdentity("1234abcd5678efgh", dob)
//        );
//    }
//
//    @Test
//    void shouldRejectIfAgeBelowMinimum() {
//        LocalDate dob = LocalDate.now().minusYears(5);
//
//        assertThrows(IllegalArgumentException.class, () ->
//                new DigitalIdentity("1234567890123456", dob)
//        );
//    }
//
//
//
//    //Test Initial State
//    @Test
//    void shouldStartInUnverifiedState() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.of(2000, 1, 1));
//
//        assertEquals(IdentityState.UNVERIFIED, identity.getState());
//    }
//
//    @Test
//    void shouldTransitionFromUnverifiedToVerified() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.of(2000, 1, 1));
//
//        identity.verify();
//
//        assertEquals(IdentityState.VERIFIED, identity.getState());
//    }
//    @Test
//    void shouldNotAllowDoubleVerification() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.of(2000, 1, 1));
//
//        identity.verify();
//
//        assertThrows(IllegalStateException.class,
//                identity::verify);
//    }
//
//    @Test
//    void adultShouldActivateWithoutConsent() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.of(2000, 1, 1));
//
//        identity.verify();
//        identity.activate(false);
//
//        assertEquals(IdentityState.ACTIVE, identity.getState());
//    }
//
//    @Test
//    void minorShouldNotActivateWithoutConsent() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.now().minusYears(15));
//
//        identity.verify();
//
//        assertThrows(IllegalStateException.class, () ->
//                identity.activate(false)
//        );
//    }
//    @Test
//    void minorShouldActivateWithConsent() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.now().minusYears(15));
//
//        identity.verify();
//        identity.activate(true);
//
//        assertEquals(IdentityState.ACTIVE, identity.getState());
//    }
//    @Test
//    void shouldNotActivateIfNotVerified() {
//        DigitalIdentity identity =
//                new DigitalIdentity("1234567890123456",
//                        LocalDate.of(2000, 1, 1));
//
//        assertThrows(IllegalStateException.class, () ->
//                identity.activate(false)
//        );
//    }
//
//
//
//
//
//
//}
