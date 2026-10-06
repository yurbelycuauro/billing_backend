package com.mobile.billing.domain.ports.in;

public interface AuthenticateUserUseCase {
    AuthenticationResult execute(LoginCommand command);

    record LoginCommand(String username, String password) {
    }

    record AuthenticationResult(String token, String refreshToken, String username, Long tenantId, long expiresIn) {
    }
}