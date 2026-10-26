package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentRequest(
        @NotBlank String paymentMethod,
        @PositiveOrZero Integer termsDays) {
}
