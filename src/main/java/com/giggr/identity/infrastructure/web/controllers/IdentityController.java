package com.giggr.identity.infrastructure.web.controllers;


import com.giggr.identity.application.IdentityService;
import com.giggr.identity.domain.identity.DateOfBirth;
import com.giggr.identity.domain.identity.Email;
import com.giggr.identity.domain.identity.EntityType;
import com.giggr.identity.domain.identity.IdentityProfile;
import com.giggr.identity.domain.verification.OtpCode;
import com.giggr.identity.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService service;

    @PostMapping("/register")
    @Operation(summary = "Step 1: Register Prospect", description = "Initializes a user profile and generates a demo OTP.")
    public RegisterResponse register(@RequestBody RegisterRequest request) {

        EntityType entityType =
                EntityType.valueOf(request.entityType().toUpperCase());

        IdentityProfile profile = new IdentityProfile(
                request.firstName(),
                request.lastName(),
                entityType,
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
    @Operation(summary = "Step 2: Verify OTP", description = "Validates the OTP. If the user is an adult, a Digital ID is assigned immediately.")
    public VerifyResponse verify(@RequestBody VerifyRequest request) {

        String digitalId = service.verify(
                new Email(request.email()),
                new OtpCode(request.otp())
        );

        return new VerifyResponse(
                "VERIFIED",
                digitalId,
                "Verification successful"
        );
    }


    @PostMapping("/consent/provide")
    @Operation(summary = "Step 2b (Minors): Provide Parent Email", description = "For users requiring consent, this links a parent email to the account.")
    public ProvideConsentResponse provideParent(@RequestBody ProvideConsentRequest request) {

        service.provideParentEmail(
                new Email(request.childEmail()),
                new Email(request.parentEmail())
        );

        return new ProvideConsentResponse(
                "CONSENT_REQUESTED",
                "Consent request sent to parent"
        );
    }


    @PostMapping("/consent/approve")
    @Operation(summary = "Step 3 (Minors): Parent Approval", description = "Simulates a parent approving the consent, which then triggers Digital ID assignment.")
    public ApproveConsentResponse approve(@RequestBody ApproveConsentRequest request) {

        String digitalId = service.approveConsent(
                new Email(request.email())
        );

        return new ApproveConsentResponse(
                "CONSENT_APPROVED",
                digitalId,
                "Consent approved successfully"
        );
    }


    @PostMapping("/terms/accept")
    @Operation(summary = "Step 4: Finalize Terms", description = "User accepts Terms & Conditions to activate their identity.")
    public AcceptTermsResponse acceptTerms(
            @RequestBody AcceptTermsRequest request) {

        service.acceptTerms(new Email(request.email()));

        return new AcceptTermsResponse(
                "TERMS_ACCEPTED",
                "Terms and Conditions accepted successfully"
        );
    }
}
