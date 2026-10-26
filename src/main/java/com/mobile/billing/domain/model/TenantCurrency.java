package com.mobile.billing.domain.model;

public record TenantCurrency(Currency currency, boolean isDefault) {

    public TenantCurrency {
        if (currency == null || !currency.active()) {
            throw new IllegalArgumentException("An active currency is required for a tenant.");
        }
    }
}
