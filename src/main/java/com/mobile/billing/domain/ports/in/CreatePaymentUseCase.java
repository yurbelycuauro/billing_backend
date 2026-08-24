package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.Payment;

public interface CreatePaymentUseCase {

    Payment createPayment(PaymenComman comman);

    record PaymenComman(
        String paymentMethod,
        Integer termsDays
    ){}



}
