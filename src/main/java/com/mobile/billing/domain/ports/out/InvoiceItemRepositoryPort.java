package com.mobile.billing.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.InvoiceItem;

public interface InvoiceItemRepositoryPort {
    InvoiceItem save(InvoiceItem invoiceItem);
    Optional<InvoiceItem> findById(Long id);
    List<InvoiceItem> findByInvoiceId(Long invoiceId);

}
