package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(
    Long id,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    Long consumptionDetailId,
    BigDecimal amount
) {}
