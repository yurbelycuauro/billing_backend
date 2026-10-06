package com.mobile.billing.infrastructure.adapter.in.rest.dto;

public record MetricDataDTO(
    String label,
    String value,
    String change,
    boolean isPositive
) {
}