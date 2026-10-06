package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

import com.mobile.billing.domain.ports.in.CreateTenantUseCase.CreateTenantCommand;

public record CreateTenantRequest(
        String countryCode,
        String companyName,
        String taxId,
        BigDecimal taxRate,
        String currencyCode,
        String address,
        String phone,
        Boolean active,
        String bankAccount) {

    public CreateTenantCommand toCommand() {
        return new CreateTenantCommand(
                countryCode,
                companyName,
                taxId,
                taxRate,
                currencyCode,
                address,
                phone,
                active == null || active,
                bankAccount);
    }
}