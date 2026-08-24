package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CustomerEntity;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByTaxId(String taxId);
    Optional<CustomerEntity> findByBusinessName(String businessName);
}
