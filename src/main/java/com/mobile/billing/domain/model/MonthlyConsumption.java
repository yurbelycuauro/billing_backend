package com.mobile.billing.domain.model;

import java.math.BigDecimal;

public record MonthlyConsumption(
    Integer year,
    Integer month,
    Long traffic,
    BigDecimal amount
) {
    public MonthlyConsumption {
        traffic = traffic != null ? traffic : 0L;
        amount = amount != null ? amount : BigDecimal.ZERO;
    }
}