package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;
import com.mobile.billing.domain.model.Payment;

public record InvoiceResponse(
    Long id,
    Long customerId,
    String invoiceNumber,
    Integer anioPeriodo,
    String mesPeriodo,
    InvoiceStatus status,
    LocalDateTime issueDate,
    List<InvoiceItem> items,
    BigDecimal netSubtotal,
    BigDecimal taxAmount,
    BigDecimal totalAmount,
    String customerName,
    String taxId,
    Moneda moneda,
    Pais pais,
    Payment payment
    
) {}
