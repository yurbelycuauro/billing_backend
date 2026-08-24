package com.mobile.billing.domain.model;

public enum InvoiceStatus {
    DRAFT,               // BORRADOR
    PREFAC_GENERATED,    // PREFAC_GENERADA (o INVOICE_GENERATED)
    SENT_TO_CUSTOMER,    // ENVIADA_A_CLIENTE
    APPROVED,            // APROBADO
    REJECTED,            // RECHAZADO
    INVOICED             // FACTURADO
}
