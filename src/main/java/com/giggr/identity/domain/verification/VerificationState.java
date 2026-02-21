package com.giggr.identity.domain.verification;

public enum VerificationState {
    NOT_STARTED,
    OTP_SENT,
    VERIFIED,
    EXPIRED,
    LOCKED
}
