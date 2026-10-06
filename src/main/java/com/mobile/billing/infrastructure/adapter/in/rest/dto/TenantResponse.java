package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

import com.mobile.billing.domain.model.Tenant;

public record TenantResponse(
        Long id,
        String countryCode,
        String companyName,
        String taxId,
        BigDecimal taxRate,
        String currencyCode,
        String address,
        String phone,
        boolean active,
        String bankAccount) {

    public static TenantResponse fromDomain(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getCountryCode(),
                tenant.getCompanyName(),
                tenant.getTaxId(),
                tenant.getTaxRate(),
                tenant.getCurrencyCode(),
                tenant.getAddress(),
                tenant.getPhone(),
                tenant.isActive(),
                tenant.getBankAccount());
    }
}