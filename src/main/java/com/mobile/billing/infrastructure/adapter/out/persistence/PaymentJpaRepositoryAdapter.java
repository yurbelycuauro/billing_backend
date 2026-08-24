package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.PaymentEntity;

@Repository
public class PaymentJpaRepositoryAdapter implements PaymentRepositoryPort {

    private final SpringDataPaymentRepository repository;

    public PaymentJpaRepositoryAdapter(SpringDataPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = repository.save(PaymentEntity.fromDomain(payment));
        return entity.toDomain();
    }

    @Override
    public Optional<Payment> getById(Long id) {
        return repository.findById(id).map(PaymentEntity::toDomain);
    }

    @Override
    public Optional<Payment> getByPaymentMethod(String paymentMethod) {
        return repository.findByPaymentMethod(paymentMethod).map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> getAll() {
        return repository.findAll().stream()
                .map(PaymentEntity::toDomain)
                .collect(Collectors.toList());
    }
}
