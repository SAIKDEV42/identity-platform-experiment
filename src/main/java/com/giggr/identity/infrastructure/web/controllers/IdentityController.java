package com.giggr.identity.infrastructure.web.controllers;


import com.giggr.identity.application.IdentityService;
import com.giggr.identity.domain.identity.DateOfBirth;
import com.giggr.identity.domain.identity.Email;
import com.giggr.identity.domain.identity.IdentityProfile;
import com.giggr.identity.domain.verification.OtpCode;
import com.giggr.identity.infrastructure.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService service;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {

        IdentityProfile profile = new IdentityProfile(
                request.firstName(),
                request.lastName(),
                new Email(request.email()),
                request.phone(),
                new DateOfBirth(LocalDate.parse(request.dob())),
                request.country()
        );

        OtpCode otp = service.register(profile);

        return new RegisterResponse(
                "OTP_SENT",
                request.email(),
                "OTP has been sent to your registered email and phone",
                otp.value() // demo only
        );
    }

    @PostMapping("/verify")
    public VerifyResponse verify(@RequestBody VerifyRequest request) {

        String digitalId = service.verify(
                new Email(request.email()),
                new OtpCode(request.otp())
        );

        return new VerifyResponse(
                "VERIFIED",
                digitalId,
                "Verification successful"
        );    }


    @PostMapping("/consent/provide")
    public ProvideConsentResponse provideParent(@RequestBody ProvideConsentRequest request) {

        service.provideParentEmail(
                new Email(request.childEmail()),
                new Email(request.parentEmail())
        );

        return new ProvideConsentResponse(
                "CONSENT_REQUESTED",
                "Consent request sent to parent"
        );    }


    @PostMapping("/consent/approve")
    public String approve(@RequestParam String email) {

        service.approveConsent(new Email(email));

        return "Consent approved";
    }
}
