package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record MetricDTO(
    MetricDataDTO data,
    List<MonthlyChartPointDTO> chartData,
    String icon,
    String color
) {
    public MetricDTO {
        chartData = List.copyOf(chartData != null ? chartData : List.of());
    }
}