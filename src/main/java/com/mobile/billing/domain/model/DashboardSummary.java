package com.mobile.billing.domain.model;

import java.util.List;

public record DashboardSummary(
    List<MonthlyBilled> totalBilled,
    List<MonthlyConsumption> consumptionByMonth,
    List<InvoiceStatusSummary> invoicesByStatus
) {
    public DashboardSummary {
        totalBilled = List.copyOf(totalBilled != null ? totalBilled : List.of());
        consumptionByMonth = List.copyOf(consumptionByMonth != null ? consumptionByMonth : List.of());
        invoicesByStatus = List.copyOf(invoicesByStatus != null ? invoicesByStatus : List.of());
    }
}