package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.ports.in.CreatePaymentUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreatePaymentService implements CreatePaymentUseCase{

    private final PaymentRepositoryPort paymentRepositoryPort;

    @Override
    public Payment createPayment(PaymenComman comman) {
        Payment payment = Payment.create(comman.paymentMethod(), comman.termsDays());
        return paymentRepositoryPort.save(payment);
    }

}
