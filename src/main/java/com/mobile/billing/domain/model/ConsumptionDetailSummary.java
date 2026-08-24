package com.mobile.billing.domain.model;

import java.math.BigDecimal;

public record ConsumptionDetailSummary(
    Long id,
    String serviceName,
    Integer traffic,
    BigDecimal unitPrice,
    BigDecimal amount
) {
    public ConsumptionDetailSummary(Long id, String serviceName, Integer traffic, BigDecimal unitPrice) {
        this(id, serviceName, traffic, unitPrice, null);
    }

    public ConsumptionDetailSummary {
        traffic = (traffic != null) ? traffic : 0;
        unitPrice = (unitPrice != null) ? unitPrice : BigDecimal.ZERO;

        if (amount == null) {
            amount = BigDecimal.valueOf(traffic).multiply(unitPrice);
        }
    }
}