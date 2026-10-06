package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.domain.model.RefreshToken;
import com.mobile.billing.domain.ports.out.RefreshTokenRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.RefreshTokenEntity;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class RefreshTokenJpaRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository refreshTokenRepository;
    private final SpringDataUserRepository userRepository;
    private final TenantContextPort tenantContext;

    public RefreshTokenJpaRepositoryAdapter(
            SpringDataRefreshTokenRepository refreshTokenRepository,
            SpringDataUserRepository userRepository,
            TenantContextPort tenantContext) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tenantContext = tenantContext;
    }

    @Override
    @Transactional
    public void save(String tokenHash, Instant expiryDate, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        refreshTokenRepository.save(new RefreshTokenEntity(tokenHash, expiryDate, user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        Long tenantContextId = tenantContext.getTenantId();
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(tokenHash).map(entity -> new RefreshToken(
                entity.getId(),
                entity.getToken(),
                entity.getExpiryDate(),
                entity.getUser().getUsername(),
                entity.getTenantId(),
                entity.getUser().getRoles()));
        String tokenFingerprint = fingerprint(tokenHash);
        if (result.isPresent()) {
            RefreshToken token = result.get();
            log.debug(
                    "Refresh token record found: tenantContextId={}, recordTenantId={}, tokenFingerprint={}, "
                            + "expiryDate={}",
                    tenantContextId,
                    token.tenantId(),
                    tokenFingerprint,
                    token.expiryDate());
        } else {
            log.warn(
                    "Refresh token database lookup returned no row: tenantContextId={}, tokenFingerprint={}",
                    tenantContextId,
                    tokenFingerprint);
        }
        return result;
    }

    @Override
    public boolean rotate(String currentTokenHash, String replacementTokenHash, Instant expiryDate) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required to rotate a refresh token.");
        }
        return refreshTokenRepository.rotateToken(
                currentTokenHash, replacementTokenHash, expiryDate, tenantId) == 1;
    }

    private String fingerprint(String tokenHash) {
        return tokenHash.substring(0, Math.min(12, tokenHash.length()));
    }
}