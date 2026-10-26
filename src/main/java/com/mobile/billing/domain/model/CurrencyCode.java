package com.mobile.billing.domain.model;

import com.mobile.billing.domain.exception.InvalidCurrencyCodeException;
import java.util.Locale;

public record CurrencyCode(String value) {

    public CurrencyCode {
        if (value == null) {
            throw new InvalidCurrencyCodeException("Currency code is required.");
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        if (!value.matches("[A-Z]{3}")) {
            throw new InvalidCurrencyCodeException("Currency code must contain three ISO 4217 letters.");
        }
    }
}
