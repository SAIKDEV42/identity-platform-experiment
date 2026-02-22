package com.giggr.identity.domain;

import com.giggr.identity.application.IdentityService;
import com.giggr.identity.application.port.EmailSender;
import com.giggr.identity.domain.identity.*;
import com.giggr.identity.domain.verification.OtpCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private IdentityRepository repository;

    @Mock
    private DigitalIdGenerator idGenerator;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private IdentityService service;

    @Test
     void shouldSendDigitalIdEmailAfterVerifyForAdult() {
        // 1. Arrange
        Email email = new Email("test@example.com");
        DigitalIdentity identity = mock(DigitalIdentity.class);
        IdentityProfile profile = mock(IdentityProfile.class); // Mock the nested profile

        when(repository.findByEmail(email)).thenReturn(Optional.of(identity));
        when(identity.requiresConsent()).thenReturn(false);
        when(identity.canAssignId()).thenReturn(true);

        // Stub the chain that was causing the NPE
        when(identity.getProfile()).thenReturn(profile);
        when(profile.email()).thenReturn(new Email("test@example.com"));
        DigitalId id = new DigitalId("001123456789012345");
        when(idGenerator.generate(identity.getProfile().entityType())).thenReturn(id);


        when(identity.getDigitalId()).thenReturn(id);
        // 2. Act
        service.verify(email, new OtpCode("123456"));

        // 3. Assert
        verify(emailSender).sendDigitalId(
                "test@example.com",
                "001123456789012345"
        );
    }
}
