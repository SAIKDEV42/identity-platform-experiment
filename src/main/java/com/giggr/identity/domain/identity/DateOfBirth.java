package com.giggr.identity.domain.identity;

import java.time.LocalDate;
import java.time.Period;

public record DateOfBirth(LocalDate value) {

    public DateOfBirth {
        if (value == null || value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid DOB");
        }
    }


//    public boolean isBelowMinimum() {
//        return years() < 8;
//    }
//
//    public boolean requiresConsent() {
//        return years() >= 8 && years() < 18;
//    }

    public int years(LocalDate today) {
        return Period.between(value, today).getYears();

    }

    public boolean isBelowMinimumAge(LocalDate today) {
        return years(today) < 8;
    }

    public boolean isMinor(LocalDate today) {
        int age = years(today);
        return age >= 8 && age < 18;
    }









}

