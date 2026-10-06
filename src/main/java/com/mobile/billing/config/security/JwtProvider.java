package com.mobile.billing.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Collection;
import java.util.Set;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.out.JwtTokenPort;

@Component
@Slf4j
public class JwtProvider implements JwtTokenPort {

    private final SecretKey signingKey;
    private final Duration tokenDuration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:PT5M}") Duration tokenDuration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenDuration = tokenDuration;
    }

    @Override
    public String generateToken(String username, Long tenantId, Set<UserRole> roles) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plus(tokenDuration);
        log.info("Access token issuedAt={}, expiration={}", issuedAt, expiration);
        return Jwts.builder()
                .subject(username)
                .claim("tenantId", tenantId)
                .claim("roles", roles.stream().map(Enum::name).collect(Collectors.toSet()))
                .claim("tokenType", "access")
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Duration accessTokenDuration() {
        return tokenDuration;
    }

    public String generateRefreshToken(String username, Long tenantId, Duration duration) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .claim("tenantId", tenantId)
                .claim("tokenType", "refresh")
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(duration)))
                .signWith(signingKey)
                .compact();
    }

    public Long getRefreshTokenTenantId(String token) {
        Claims claims;
        try {
            claims = parseClaims(token);
        } catch (RuntimeException exception) {
            log.warn("Refresh JWT parsing, signature, or expiration validation failed ({})",
                    exception.getClass().getSimpleName());
            throw new IllegalArgumentException("Invalid refresh token.", exception);
        }
        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            log.warn("JWT rejected for refresh: tokenType is not refresh");
            throw new IllegalArgumentException("Invalid refresh token.");
        }
        Object tenantClaim = claims.get("tenantId", Object.class);
        if (tenantClaim == null) {
            log.warn("JWT rejected for refresh: tenantId claim is missing");
            throw new IllegalArgumentException("Invalid refresh token.");
        }
        try {
            return Long.valueOf(tenantClaim.toString());
        } catch (NumberFormatException exception) {
            log.warn("JWT rejected for refresh: tenantId claim is not numeric");
            throw new IllegalArgumentException("Invalid refresh token.", exception);
        }
    }

    public boolean validate(String token) {
        try {
            return !"refresh".equals(parseClaims(token).get("tokenType", String.class));
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getTenantId(String token) {
        Claims claims = parseClaims(token);
        Object tenantClaim = claims.get("tenantId", Object.class);
        if (tenantClaim == null) {
            tenantClaim = claims.get("tenant_id", Object.class);
        }
        if (tenantClaim == null) {
            return null;
        }
        return Long.valueOf(tenantClaim.toString());
    }

    public Set<String> getRoles(String token) {
        Object rolesClaim = parseClaims(token).get("roles");
        if (!(rolesClaim instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }
        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}