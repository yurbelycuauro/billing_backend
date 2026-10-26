package com.mobile.billing.domain.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(String fromCurrency, String toCurrency, String rateDate) {
        super("No exchange rate found for " + fromCurrency + " to " + toCurrency + " on " + rateDate + ".");
    }
}
