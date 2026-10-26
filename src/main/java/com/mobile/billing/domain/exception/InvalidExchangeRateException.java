package com.mobile.billing.domain.exception;

public class InvalidExchangeRateException extends RuntimeException {

    public InvalidExchangeRateException(String message) {
        super(message);
    }
}
