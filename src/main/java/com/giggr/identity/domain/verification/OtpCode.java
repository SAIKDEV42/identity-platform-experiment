package com.giggr.identity.domain.verification;


import java.util.Objects;

public record OtpCode(String value) {

    public OtpCode {
        Objects.requireNonNull(value, "OTP cannot be null");

        if (!value.matches("\\d{6}")) {
            throw new IllegalArgumentException("OTP must be 6 digits");
        }
    }

    @Override
    public String toString() {
        return "******"; // prevent accidental logging
    }
}

