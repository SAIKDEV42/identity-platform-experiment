package com.giggr.identity.domain.identity;

import java.time.LocalDate;
import java.time.Period;

public record DateOfBirth(LocalDate value) {

    public DateOfBirth {
        if (value == null || value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid DOB");
        }
    }

    public int years(LocalDate today) {
        return Period.between(value, today).getYears();
    }

//    public boolean isBelowMinimum() {
//        return years() < 8;
//    }
//
//    public boolean requiresConsent() {
//        return years() >= 8 && years() < 18;
//    }
}

