package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.ports.in.AuthenticateUserUseCase.AuthenticationResult;
import com.mobile.billing.domain.ports.in.RefreshTokenUseCase.RefreshResult;

public record LoginResponse(
        String token, String refreshToken, String tokenType, String username, Long tenantId, long expiresIn) {

    private static final String TOKEN_TYPE = "Bearer";

    public static LoginResponse from(AuthenticationResult result) {
        return new LoginResponse(
                result.token(), result.refreshToken(), TOKEN_TYPE, result.username(), result.tenantId(),
                result.expiresIn());
    }

    public static LoginResponse from(RefreshResult result) {
        return new LoginResponse(
                result.token(), result.refreshToken(), TOKEN_TYPE, result.username(), result.tenantId(),
                result.expiresIn());
    }
}