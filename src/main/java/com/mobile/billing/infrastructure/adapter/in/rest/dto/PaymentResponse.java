package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.Payment;

public record PaymentResponse(
        Long id,
        Long tenantId,
        String paymentMethod,
        Integer termsDays) {

    public static PaymentResponse fromDomain(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTenantId(),
                payment.getPaymentMethod(),
                payment.getTermsDays());
    }
}
