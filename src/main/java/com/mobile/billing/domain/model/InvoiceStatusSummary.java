package com.mobile.billing.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record InvoiceStatusSummary(
    String status,
    String percentage,
    long count,
    int value,
    String color,
    String type
) {
    public static InvoiceStatusSummary from(InvoiceStatus status, long count, long totalCount) {
        int percentageValue = calculatePercentage(count, totalCount);

        return new InvoiceStatusSummary(
            status.label(),
                percentageValue + "%",
                count,
                percentageValue,
                status.color(),
                status.type());
    }

    private static int calculatePercentage(long count, long totalCount) {
        if (totalCount <= 0 || count <= 0) {
            return 0;
        }

        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 0, RoundingMode.HALF_UP)
                .intValue();
    }
}