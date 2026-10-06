package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.model.UserRole;
import java.util.Set;

public interface CreateUserUseCase {
    User execute(CreateUserCommand command);

    record CreateUserCommand(Long tenantId, String username, String password, Set<UserRole> roles) { }
}