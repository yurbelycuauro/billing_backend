package com.mobile.billing.domain.model;

import com.mobile.billing.domain.exception.InvalidExchangeRateException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CurrencyExchangeRate(
        UUID id,
        Long tenantId,
        CurrencyCode fromCurrency,
        CurrencyCode toCurrency,
        BigDecimal rate,
        LocalDate rateDate) {

    public CurrencyExchangeRate {
        if (id == null) {
            throw new IllegalArgumentException("Exchange rate ID is required.");
        }
        if (fromCurrency == null || toCurrency == null) {
            throw new InvalidExchangeRateException("Both currency codes are required.");
        }
        if (fromCurrency.equals(toCurrency)) {
            throw new InvalidExchangeRateException("Source and target currencies must be different.");
        }
        if (rate == null || rate.signum() <= 0) {
            throw new InvalidExchangeRateException("Exchange rate must be greater than zero.");
        }
        if (rate.precision() > 18 || Math.max(0, rate.scale()) > 6) {
            throw new InvalidExchangeRateException("Exchange rate supports at most 18 digits and 6 decimals.");
        }
        if (rateDate == null) {
            throw new InvalidExchangeRateException("Rate date is required.");
        }
    }
}
