package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.util.List;

import com.mobile.billing.domain.model.InvoiceStatusSummary;
import com.mobile.billing.domain.model.MonthlyBilled;
import com.mobile.billing.domain.model.MonthlyConsumption;

public record DashboardResponse(
    List<MonthlyBilled> totalBilled,
    List<MonthlyConsumption> consumptionByMonth,
    List<InvoiceStatusSummary> invoicesByStatus,
    List<MetricDTO> metrics
) {
    public DashboardResponse {
        totalBilled = List.copyOf(totalBilled != null ? totalBilled : List.of());
        consumptionByMonth = List.copyOf(consumptionByMonth != null ? consumptionByMonth : List.of());
        invoicesByStatus = List.copyOf(invoicesByStatus != null ? invoicesByStatus : List.of());
        metrics = List.copyOf(metrics != null ? metrics : List.of());
    }
}