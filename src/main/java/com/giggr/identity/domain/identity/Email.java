package com.giggr.identity.domain.identity;


import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final List<String> FORBIDDEN_DOMAINS =
            List.of("gmail.com", "yahoo.com", "hotmail.com", "outlook.com");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email {
        Objects.requireNonNull(value, "Email required");

        if (value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        value = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public boolean isPublicProvider() {
        String domain = value.substring(value.lastIndexOf("@") + 1).toLowerCase();
        return FORBIDDEN_DOMAINS.contains(domain);
    }
    @Override
    public String toString() {
        return value;
    }
}

