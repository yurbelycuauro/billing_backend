package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.util.List;

import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;

public record GenerateInvoicePdfRequest(
    Long clientId,
    Integer anioPeriodo,
    Integer mesPeriodo,
    Long paymentId,
    Moneda moneda,
    Pais pais,
    List<InvoiceItem> items
) {}
