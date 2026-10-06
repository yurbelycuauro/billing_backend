package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.model.UserRole;
import java.util.Set;

public interface UpdateUserUseCase {
    User execute(Long id, UpdateUserCommand command);

    record UpdateUserCommand(String password, Set<UserRole> roles, Boolean active) { }
}