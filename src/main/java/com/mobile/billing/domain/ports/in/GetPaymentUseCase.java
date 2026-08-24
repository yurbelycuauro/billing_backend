package com.mobile.billing.domain.ports.in;


import java.util.Optional;

import com.mobile.billing.domain.model.Payment;

public interface GetPaymentUseCase {
    
    Optional<Payment> getPaymentById(Long id);
    Optional<Payment> getPaymentByMethod(String paymentMethod);

}
