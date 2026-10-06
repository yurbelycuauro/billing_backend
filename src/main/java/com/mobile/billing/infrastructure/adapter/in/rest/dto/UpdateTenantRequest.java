package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

import com.mobile.billing.domain.ports.in.UpdateTenantUseCase.UpdateTenantCommand;

public record UpdateTenantRequest(
        String companyName,
        String taxId,
        BigDecimal taxRate,
        String currencyCode,
        String address,
        String phone,
        boolean active,
        String bankAccount) {

    public UpdateTenantCommand toCommand(Long tenantId) {
        return new UpdateTenantCommand(
                tenantId,
                companyName,
                taxId,
                taxRate,
                currencyCode,
                address,
                phone,
                active,
                bankAccount);
    }
}