package com.giggr.identity.infrastructure.persistence.jpa;

import com.giggr.identity.domain.consent.ConsentState;
import com.giggr.identity.domain.identity.EntityType;
import com.giggr.identity.domain.identity.IdentityState;
import com.giggr.identity.domain.verification.VerificationState;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "digital_identity")
public class JpaDigitalIdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

private  boolean termsAccepted;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    private boolean consentRequired;


    private String phone;
    private String country;



    @Enumerated(EnumType.STRING)
    private IdentityState state;

    @Column(unique = true)
    private String digitalId;

    // verification
    @Enumerated(EnumType.STRING)
    private VerificationState verificationState;
    private String otpCode;
    private Instant otpExpiry;
    private int otpAttempts;
    private Instant lastSeenAt;

    // consent
    @Enumerated(EnumType.STRING)
    private ConsentState consentState;
    private String parentEmail;
    private Instant consentApprovedAt;

    // getters + setters


    public Instant getConsentApprovedAt() {
        return consentApprovedAt;
    }

    public void setConsentApprovedAt(Instant consentApprovedAt) {
        this.consentApprovedAt = consentApprovedAt;
    }

    public ConsentState getConsentState() {
        return consentState;
    }

    public void setConsentState(ConsentState consentState) {
        this.consentState = consentState;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getOtpAttempts() {
        return otpAttempts;
    }

    public void setOtpAttempts(int otpAttempts) {
        this.otpAttempts = otpAttempts;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Instant getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(Instant otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public IdentityState getState() {
        return state;
    }

    public void setState(IdentityState state) {
        this.state = state;
    }

    public VerificationState getVerificationState() {
        return verificationState;
    }

    public boolean isConsentRequired() {
        return consentRequired;
    }

    public void setConsentRequired(boolean consentRequired) {
        this.consentRequired = consentRequired;
    }

    public void setVerificationState(VerificationState verificationState) {
        this.verificationState = verificationState;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}

