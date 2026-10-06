package com.mobile.billing.application;

import com.mobile.billing.domain.ports.in.AuthenticateUserUseCase;
import com.mobile.billing.domain.ports.in.IssueRefreshTokenUseCase;
import com.mobile.billing.domain.ports.out.AuthenticationPort;
import com.mobile.billing.domain.ports.out.JwtTokenPort;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final AuthenticationPort authentication;
    private final JwtTokenPort tokens;
    private final IssueRefreshTokenUseCase refreshTokens;

    public AuthenticateUserService(
            AuthenticationPort authentication,
            JwtTokenPort tokens,
            IssueRefreshTokenUseCase refreshTokens) {
        this.authentication = authentication;
        this.tokens = tokens;
        this.refreshTokens = refreshTokens;
    }

    @Override
    public AuthenticationResult execute(LoginCommand command) {
        AuthenticationPort.AuthenticatedUser user = authentication.authenticate(
                command.username(), command.password());
        String token = tokens.generateToken(user.username(), user.tenantId(), user.roles());
        String refreshToken = refreshTokens.issue(user.username(), user.tenantId());
        return new AuthenticationResult(
                token, refreshToken, user.username(), user.tenantId(), tokens.accessTokenDuration().toSeconds());
    }
}