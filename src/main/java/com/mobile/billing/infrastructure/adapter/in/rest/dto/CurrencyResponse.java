package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.Currency;

public record CurrencyResponse(String code, String name, String symbol, int decimalDigits, boolean active) {

    public static CurrencyResponse fromDomain(Currency currency) {
        return new CurrencyResponse(
                currency.code().value(),
                currency.name(),
                currency.symbol(),
                currency.decimalDigits(),
                currency.active());
    }
}
