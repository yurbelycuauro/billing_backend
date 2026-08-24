package com.mobile.billing.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.Payment;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);
    Optional<Payment> getById(Long id);
    Optional<Payment> getByPaymentMethod(String paymentMethod);
    List<Payment> getAll();
}
