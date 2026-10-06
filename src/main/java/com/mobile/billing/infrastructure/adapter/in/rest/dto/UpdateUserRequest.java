package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.in.UpdateUserUseCase.UpdateUserCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserRequest(
        @Size(min = 8) String password,
        @NotEmpty Set<UserRole> roles,
        Boolean active
) {
    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(password, roles, active);
    }
}