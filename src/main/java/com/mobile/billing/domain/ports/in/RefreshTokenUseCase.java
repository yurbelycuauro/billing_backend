package com.mobile.billing.domain.ports.in;

public interface RefreshTokenUseCase {
    RefreshResult refresh(String refreshToken);

    record RefreshResult(String token, String refreshToken, String username, Long tenantId, long expiresIn) {
    }
}