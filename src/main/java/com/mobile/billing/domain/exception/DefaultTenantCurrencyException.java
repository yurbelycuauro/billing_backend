package com.mobile.billing.domain.exception;

public class DefaultTenantCurrencyException extends RuntimeException {

    public DefaultTenantCurrencyException(String currencyCode) {
        super("Set another default currency before disabling " + currencyCode + ".");
    }
}
