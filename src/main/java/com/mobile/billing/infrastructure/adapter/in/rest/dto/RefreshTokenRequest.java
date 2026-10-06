package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
                @NotBlank String refreshToken) {
}