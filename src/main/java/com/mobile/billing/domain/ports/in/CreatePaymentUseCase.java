package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.Payment;

public interface CreatePaymentUseCase {

    Payment createPayment(CreatePaymentCommand command);

    record CreatePaymentCommand(String paymentMethod, Integer termsDays) {
    }
}
