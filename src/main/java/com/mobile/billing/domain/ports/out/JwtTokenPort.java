package com.mobile.billing.domain.ports.out;

import java.time.Duration;
import java.util.Set;
import com.mobile.billing.domain.model.UserRole;

public interface JwtTokenPort {
    String generateToken(String username, Long tenantId, Set<UserRole> roles);

    Duration accessTokenDuration();

    String generateRefreshToken(String username, Long tenantId, Duration duration);

    Long getRefreshTokenTenantId(String token);
}