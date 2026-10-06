package com.mobile.billing.application;

import com.mobile.billing.domain.model.RefreshToken;
import com.mobile.billing.domain.ports.in.IssueRefreshTokenUseCase;
import com.mobile.billing.domain.ports.in.RefreshTokenUseCase;
import com.mobile.billing.domain.ports.out.JwtTokenPort;
import com.mobile.billing.domain.ports.out.RefreshTokenRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefreshTokenService implements IssueRefreshTokenUseCase, RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokens;
    private final JwtTokenPort jwtTokens;
    private final TenantContextPort tenantContext;
    private final Duration refreshTokenDuration;

    public RefreshTokenService(
            RefreshTokenRepositoryPort refreshTokens,
            JwtTokenPort jwtTokens,
            TenantContextPort tenantContext,
            @Value("${jwt.refresh-expiration:PT168H}") Duration refreshTokenDuration) {
        this.refreshTokens = refreshTokens;
        this.jwtTokens = jwtTokens;
        this.tenantContext = tenantContext;
        this.refreshTokenDuration = refreshTokenDuration;
    }

    @Override
    public String issue(String username, Long tenantId) {
        requireTenant(tenantId);
        tenantContext.setTenantId(tenantId);
        try {
            String token = jwtTokens.generateRefreshToken(username, tenantId, refreshTokenDuration);
            String tokenHash = hash(token);
            refreshTokens.save(tokenHash, Instant.now().plus(refreshTokenDuration), username);
            log.info(
                    "Refresh token issued and persisted: tenantId={}, tokenFingerprint={}",
                    tenantId,
                    fingerprint(tokenHash));
            return token;
        } finally {
            tenantContext.clear();
        }
    }

    @Override
    public RefreshResult refresh(String refreshToken) {
        Long tenantId = jwtTokens.getRefreshTokenTenantId(refreshToken);
        requireTenant(tenantId);
        tenantContext.setTenantId(tenantId);
        try {
            String currentTokenHash = hash(refreshToken);
            String currentFingerprint = fingerprint(currentTokenHash);
            log.info("Refresh attempt received: tenantId={}, tokenFingerprint={}", tenantId, currentFingerprint);
            RefreshToken storedToken = refreshTokens.findByTokenHash(currentTokenHash)
                    .orElseThrow(() -> {
                        log.warn(
                                "Refresh token lookup returned no record: tenantId={}, tokenFingerprint={}; "
                                        + "compare with the issued/persisted fingerprint to identify token reuse "
                                        + "or missing persistence",
                                tenantId,
                                currentFingerprint);
                        return new IllegalArgumentException("Invalid refresh token.");
                    });
            if (!tenantId.equals(storedToken.tenantId())) {
                log.warn("Refresh token tenant mismatch: claimTenantId={}, storedTenantId={}",
                        tenantId, storedToken.tenantId());
                throw new IllegalArgumentException("Invalid refresh token.");
            }
            try {
                storedToken.verifyExpiration(Instant.now());
            } catch (IllegalArgumentException exception) {
                log.warn("Stored refresh token is expired for tenantId={}", tenantId);
                throw exception;
            }

            String replacementToken = jwtTokens.generateRefreshToken(
                    storedToken.username(), storedToken.tenantId(), refreshTokenDuration);
            String replacementTokenHash = hash(replacementToken);
            Instant replacementExpiry = Instant.now().plus(refreshTokenDuration);
            if (!refreshTokens.rotate(currentTokenHash, replacementTokenHash, replacementExpiry)) {
                log.warn(
                        "Refresh token rotation failed: tenantId={}, tokenFingerprint={}; token may have been reused",
                        tenantId,
                        currentFingerprint);
                throw new IllegalArgumentException("Refresh token has already been used.");
            }

            String accessToken = jwtTokens.generateToken(
                    storedToken.username(), storedToken.tenantId(), storedToken.roles());
            log.info(
                    "Refresh token rotated: tenantId={}, previousFingerprint={}, replacementFingerprint={}",
                    tenantId,
                    currentFingerprint,
                    fingerprint(replacementTokenHash));
            return new RefreshResult(
                    accessToken,
                    replacementToken,
                    storedToken.username(),
                    storedToken.tenantId(),
                    jwtTokens.accessTokenDuration().toSeconds());
        } finally {
            tenantContext.clear();
        }
    }

    private void requireTenant(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID is required.");
        }
    }

    private String fingerprint(String tokenHash) {
        return tokenHash.substring(0, Math.min(12, tokenHash.length()));
    }

    private String hash(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required.");
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }

}
