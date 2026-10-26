package com.mobile.billing.domain.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency is not registered or active: " + code);
    }
}
