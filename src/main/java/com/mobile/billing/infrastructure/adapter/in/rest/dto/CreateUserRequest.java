package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.in.CreateUserUseCase.CreateUserCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateUserRequest(
        @NotNull Long tenantId,
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password,
        @NotEmpty Set<UserRole> roles
) {
    public CreateUserCommand toCommand() {
        return new CreateUserCommand(tenantId, username, password, roles);
    }
}