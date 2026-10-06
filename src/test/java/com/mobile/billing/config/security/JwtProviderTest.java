package com.mobile.billing.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mobile.billing.domain.model.UserRole;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private final JwtProvider provider = new JwtProvider(
            "01234567890123456789012345678901", Duration.ofHours(1));

    @Test
    void refreshTokenCarriesSignedTenantId() {
        String token = provider.generateRefreshToken("user@example.com", 42L, Duration.ofDays(7));

        assertEquals(42L, provider.getRefreshTokenTenantId(token));
    }

    @Test
    void accessTokenCannotBeUsedAsRefreshToken() {
        String token = provider.generateToken("user@example.com", 42L, Set.<UserRole>of());

        assertThrows(IllegalArgumentException.class, () -> provider.getRefreshTokenTenantId(token));
    }

    @Test
    void refreshTokenIsNotAcceptedAsAccessToken() {
        String token = provider.generateRefreshToken("user@example.com", 42L, Duration.ofDays(7));

        assertFalse(provider.validate(token));
    }
}