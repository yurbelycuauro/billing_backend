package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.TenantCurrency;

public record TenantCurrencyResponse(
        String code,
        String name,
        String symbol,
        int decimalDigits,
        boolean isDefault) {

    public static TenantCurrencyResponse fromDomain(TenantCurrency tenantCurrency) {
        var currency = tenantCurrency.currency();
        return new TenantCurrencyResponse(
                currency.code().value(),
                currency.name(),
                currency.symbol(),
                currency.decimalDigits(),
                tenantCurrency.isDefault());
    }
}
