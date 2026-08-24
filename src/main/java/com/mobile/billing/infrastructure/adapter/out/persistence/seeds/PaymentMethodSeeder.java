package com.mobile.billing.infrastructure.adapter.out.persistence.seeds;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mobile.billing.infrastructure.adapter.out.persistence.SpringDataPaymentRepository;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.PaymentEntity;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PaymentMethodSeeder implements CommandLineRunner {

    private final SpringDataPaymentRepository springDataPaymentRepository;

    @Override
    public void run(String... args) throws Exception {
        seedPaymentMethods();
    }

    private void seedPaymentMethods(){
        List<PaymentEntity> defaultMethod = List.of(
            new PaymentEntity(null, "Efectivo", 0),
            new PaymentEntity(null, "Tarjeta de Crédito", 0),
            new PaymentEntity(null, "Crédito a 30 días", 30),
            new PaymentEntity(null, "Crédito a 60 días", 60)
        );

        for(PaymentEntity payment:defaultMethod){
            if (!springDataPaymentRepository.existsByPaymentMethod(payment.getPaymentMethod())) {
                springDataPaymentRepository.save(payment);
            }
        }
    }

}
