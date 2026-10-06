package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.RefreshToken;
import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    void save(String tokenHash, Instant expiryDate, String username);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    boolean rotate(String currentTokenHash, String replacementTokenHash, Instant expiryDate);
}