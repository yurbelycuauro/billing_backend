package com.mobile.billing.domain.exception;

public class TenantCurrencyNotFoundException extends RuntimeException {

    public TenantCurrencyNotFoundException(String currencyCode) {
        super("Currency is not enabled for this tenant: " + currencyCode);
    }
}
