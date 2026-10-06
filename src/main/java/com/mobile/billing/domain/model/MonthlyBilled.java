package com.mobile.billing.domain.model;

import java.math.BigDecimal;

public record MonthlyBilled(
    Integer year,
    Integer month,
    BigDecimal amount
) {
    public MonthlyBilled {
        amount = amount != null ? amount : BigDecimal.ZERO;
    }
}