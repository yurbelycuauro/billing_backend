package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.ports.out.InvoiceItemRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceItemEntity;

@Repository
public class InvoiceItemJpaRepositoryAdapter implements InvoiceItemRepositoryPort {

    private final SpringDataInvoiceItemRepository repository;

    public InvoiceItemJpaRepositoryAdapter(SpringDataInvoiceItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public InvoiceItem save(InvoiceItem invoiceItem) {
        InvoiceItemEntity entity = repository.save(InvoiceItemEntity.fromDomain(invoiceItem, null));
        return entity.toDomain();
    }

    @Override
    public Optional<InvoiceItem> findById(Long id) {
        return repository.findById(id).map(InvoiceItemEntity::toDomain);
    }

    @Override
    public List<InvoiceItem> findByInvoiceId(Long invoiceId) {
        return repository.findByInvoiceId(invoiceId).stream()
                .map(InvoiceItemEntity::toDomain)
                .collect(Collectors.toList());
    }
}
