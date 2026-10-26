package com.mobile.billing.domain.exception;

public class TenantCurrencyNotAllowedException extends RuntimeException {

    public TenantCurrencyNotAllowedException(String currencyCode) {
        super("Currency is not enabled for this tenant: " + currencyCode);
    }
}
