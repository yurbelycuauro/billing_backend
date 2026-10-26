package com.mobile.billing.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.Payment;

public interface GetPaymentUseCase {

    Optional<Payment> getPaymentById(Long id);

    Optional<Payment> getPaymentByMethod(String paymentMethod);

    List<Payment> getPaymentsByTenantId(Long tenantId);

    List<Payment> getAllPayments();

}
