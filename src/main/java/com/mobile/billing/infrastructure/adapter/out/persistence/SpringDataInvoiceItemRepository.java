package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceItemEntity;

public interface SpringDataInvoiceItemRepository extends JpaRepository<InvoiceItemEntity, Long> {
    List<InvoiceItemEntity> findByInvoiceId(Long invoiceId);
}
