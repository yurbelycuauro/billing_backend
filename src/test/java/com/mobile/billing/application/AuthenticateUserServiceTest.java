package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.in.AuthenticateUserUseCase.LoginCommand;
import com.mobile.billing.domain.ports.in.IssueRefreshTokenUseCase;
import com.mobile.billing.domain.ports.out.AuthenticationPort;
import com.mobile.billing.domain.ports.out.JwtTokenPort;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AuthenticateUserServiceTest {

    @Test
    void loginReturnsAccessAndRefreshTokensForAuthenticatedTenant() {
        AuthenticationPort authentication = mock(AuthenticationPort.class);
        JwtTokenPort jwtTokens = mock(JwtTokenPort.class);
        IssueRefreshTokenUseCase refreshTokens = mock(IssueRefreshTokenUseCase.class);
        AuthenticateUserService service = new AuthenticateUserService(authentication, jwtTokens, refreshTokens);
        when(authentication.authenticate("user@example.com", "secret"))
                .thenReturn(new AuthenticationPort.AuthenticatedUser(
                        "user@example.com", 42L, Set.of(UserRole.USER)));
        when(jwtTokens.generateToken("user@example.com", 42L, Set.of(UserRole.USER))).thenReturn("access");
        when(jwtTokens.accessTokenDuration()).thenReturn(Duration.ofHours(1));
        when(refreshTokens.issue("user@example.com", 42L)).thenReturn("refresh");

        var result = service.execute(new LoginCommand("user@example.com", "secret"));

        assertEquals("access", result.token());
        assertEquals("refresh", result.refreshToken());
        assertEquals(42L, result.tenantId());
        assertEquals(3600L, result.expiresIn());
        verify(authentication).authenticate("user@example.com", "secret");
    }
}