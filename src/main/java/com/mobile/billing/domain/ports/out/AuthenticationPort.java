package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.UserRole;
import java.util.Set;

public interface AuthenticationPort {
    AuthenticatedUser authenticate(String username, String password);

    record AuthenticatedUser(String username, Long tenantId, Set<UserRole> roles) {
    }
}