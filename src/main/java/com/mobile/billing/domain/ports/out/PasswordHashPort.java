package com.mobile.billing.domain.ports.out;

public interface PasswordHashPort {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String passwordHash);
}