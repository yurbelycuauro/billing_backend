package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.PaymentEntity;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentMethod(String paymentMethod);

    boolean existsByPaymentMethod(String method);

    List<PaymentEntity> findByTenantId(Long tenantId);
}
