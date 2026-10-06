package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.ports.in.AuthenticateUserUseCase.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password) {
    public LoginCommand toCommand() {
        return new LoginCommand(username, password);
    }
}