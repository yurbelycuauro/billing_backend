package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import com.mobile.billing.domain.model.RefreshToken;
import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.out.JwtTokenPort;
import com.mobile.billing.domain.ports.out.RefreshTokenRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.LoginResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RefreshTokenServiceTest {

    private final RefreshTokenRepositoryPort refreshTokens = mock(RefreshTokenRepositoryPort.class);
    private final JwtTokenPort jwtTokens = mock(JwtTokenPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final RefreshTokenService service = new RefreshTokenService(
            refreshTokens, jwtTokens, tenantContext, Duration.ofDays(7));

    @Test
    void issueStoresOnlyTokenHashUnderRequestedTenant() {
        when(jwtTokens.generateRefreshToken("user@example.com", 42L, Duration.ofDays(7))).thenReturn("refresh");
        String token = service.issue("user@example.com", 42L);
        ArgumentCaptor<String> tokenHash = ArgumentCaptor.forClass(String.class);

        verify(tenantContext).setTenantId(42L);
        verify(refreshTokens).save(tokenHash.capture(), any(Instant.class), eq("user@example.com"));
        assertNotEquals(token, tokenHash.getValue());
        verify(tenantContext).clear();
    }

    @Test
    void refreshRotatesTokenAndCreatesTenantScopedAccessToken() {
        RefreshToken storedToken = new RefreshToken(
                1L, "hash", Instant.now().plusSeconds(60), "user@example.com", 42L, Set.of(UserRole.USER));
        when(jwtTokens.getRefreshTokenTenantId("old-refresh")).thenReturn(42L);
        when(jwtTokens.generateRefreshToken("user@example.com", 42L, Duration.ofDays(7)))
                .thenReturn("replacement-refresh");
        when(refreshTokens.findByTokenHash(anyString())).thenReturn(Optional.of(storedToken));
        when(refreshTokens.rotate(anyString(), anyString(), any(Instant.class))).thenReturn(true);
        when(jwtTokens.generateToken("user@example.com", 42L, Set.of(UserRole.USER))).thenReturn("new-access");
        when(jwtTokens.accessTokenDuration()).thenReturn(Duration.ofHours(1));

        var result = service.refresh("old-refresh");
        var response = LoginResponse.from(result);

        assertEquals("new-access", result.token());
        assertEquals("replacement-refresh", result.refreshToken());
        assertEquals("replacement-refresh", response.refreshToken());
        assertEquals(42L, result.tenantId());
        assertEquals(3600L, result.expiresIn());
        verify(refreshTokens).rotate(anyString(), anyString(), any(Instant.class));
        verify(tenantContext).clear();
    }

    @Test
    void refreshRejectsExpiredTokenAndClearsTenantContext() {
        RefreshToken expiredToken = new RefreshToken(
                1L, "hash", Instant.now().minusSeconds(1), "user@example.com", 42L, Set.of(UserRole.USER));
        when(jwtTokens.getRefreshTokenTenantId("expired-refresh")).thenReturn(42L);
        when(refreshTokens.findByTokenHash(anyString())).thenReturn(Optional.of(expiredToken));

        assertThrows(IllegalArgumentException.class, () -> service.refresh("expired-refresh"));

        verify(refreshTokens, never()).rotate(anyString(), anyString(), any(Instant.class));
        verify(jwtTokens, never()).generateToken(anyString(), any(), any());
        verify(tenantContext).clear();
    }
}