package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.ports.in.CreatePaymentUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;

@Service
public class CreatePaymentService implements CreatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public CreatePaymentService(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    public Payment createPayment(CreatePaymentCommand command) {
        Payment payment = Payment.create(command.paymentMethod(), command.termsDays());
        return paymentRepositoryPort.save(payment);
    }

}
