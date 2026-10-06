package com.mobile.billing.domain.model;

import java.time.Instant;
import java.util.Set;

public record RefreshToken(
        Long id,
        String tokenHash,
        Instant expiryDate,
        String username,
        Long tenantId,
        Set<UserRole> roles) {

    public RefreshToken {
        roles = Set.copyOf(roles);
    }

    public void verifyExpiration(Instant now) {
        if (expiryDate == null || !expiryDate.isAfter(now)) {
            throw new IllegalArgumentException("Refresh token expired.");
        }
    }
}