package com.giggr.identity.domain.verification;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Verification {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration OTP_VALIDITY = Duration.ofMinutes(5);
    private static final Duration RESEND_THROTTLE = Duration.ofSeconds(30);

    private final Clock clock;


    private VerificationState state;
    private OtpCode otpCode;
    private Instant otpExpiry;
    private Instant lastSentAt;
    private int attempts;

    public Verification(Clock clock) {
        this.clock = clock;
        this.state = VerificationState.NOT_STARTED;
    }

    // only for loading
    public Verification(
            VerificationState state,
            String otpCode,
            Instant otpExpiry,
            int attempts,
            Instant lastSentAt,
            Clock clock
    ) {
        this.state = state;
        this.otpCode = otpCode != null ? new OtpCode(otpCode) : null;
        this.otpExpiry = otpExpiry;
        this.attempts = attempts;
        this.clock = clock;
    }


    public OtpCode sendOtp() {
        if (state == VerificationState.VERIFIED) {
            throw new IllegalStateException("Already verified");
        }

        Instant now = Instant.now(clock);

        if (lastSentAt != null) {
            Duration timeSinceLastSent = Duration.between(lastSentAt, now);
            if (timeSinceLastSent.compareTo(RESEND_THROTTLE) < 0) {// first obj smaller than the second if -ve
                long secondsToWait = RESEND_THROTTLE.getSeconds() - timeSinceLastSent.getSeconds();
                throw new IllegalStateException("Please wait " + secondsToWait + " seconds before requesting a new OTP");
            }
        }


        if (lastSentAt != null &&
                now.isBefore(lastSentAt.plus(RESEND_THROTTLE))) {
            throw new IllegalStateException("OTP resend too soon");
        }

        this.otpCode = generateOtp();
        this.otpExpiry = now.plus(OTP_VALIDITY);
        this.lastSentAt = now;
        this.attempts = 0;
        this.state = VerificationState.OTP_SENT;
        return this.otpCode;

    }

    public void verify(OtpCode inputOtp) {
        if (state == VerificationState.VERIFIED) {
            //return; // idempotent
            throw new IllegalStateException("Verification already completed");
        }

        if (state != VerificationState.OTP_SENT) {
            throw new IllegalStateException("not active OTP found");
        }

        Instant now = Instant.now(clock);

        if (now.isAfter(otpExpiry)) {
            state = VerificationState.EXPIRED;
            throw new IllegalStateException("OTP expired");
        }

        if (attempts >= MAX_ATTEMPTS) {
            state = VerificationState.LOCKED;
            throw new IllegalStateException("Max attempts exceeded");
        }

        attempts++;

        if (!Objects.equals(otpCode, inputOtp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        state = VerificationState.VERIFIED;
        this.otpCode = null; // clear sensitive data god practiseu
    }

    public boolean isVerified() {
        return state == VerificationState.VERIFIED;
    }

    public VerificationState getState() {
        return state;
    }

    public int getAttempts() {
        return attempts;
    }

    public OtpCode getOtpCode() {
        return otpCode;
    }

    public Instant getLastSentAt() {
        return lastSentAt;
    }

    public Instant getOtpExpiry() {
        return otpExpiry;
    }


    private OtpCode generateOtp() {

        int number = RANDOM.nextInt(900000) + 100000;
        return new OtpCode(String.valueOf(number));
    }
}

