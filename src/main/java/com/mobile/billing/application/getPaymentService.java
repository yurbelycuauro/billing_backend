package com.mobile.billing.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.ports.in.GetPaymentUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class getPaymentService implements GetPaymentUseCase{

    private final PaymentRepositoryPort paymentRepositoryPort;

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepositoryPort.getById(id);
       
    }

    @Override
    public Optional<Payment> getPaymentByMethod(String paymentMethod) {
        return paymentRepositoryPort.getByPaymentMethod(paymentMethod);
        
    }

}
