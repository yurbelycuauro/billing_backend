package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceEntity;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByIssueDateBetween(LocalDateTime start, LocalDateTime end);
    List<InvoiceEntity> findByCustomer_IdAndIssueDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);
}
