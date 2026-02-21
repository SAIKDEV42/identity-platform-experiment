package com.giggr.identity.domain.identity;

public record DigitalId(String value) {

    public DigitalId {
        if (value == null || !value.matches("\\d{18}")) {
            throw new IllegalArgumentException("Digital ID must be 18 digits");
        }
    }
}

