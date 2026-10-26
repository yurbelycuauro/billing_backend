package com.mobile.billing.domain.exception;

public class DuplicateExchangeRateException extends RuntimeException {

    public DuplicateExchangeRateException() {
        super("An exchange rate already exists for this tenant, currency pair, and date.");
    }
}
