package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.CurrencyExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExchangeRateResponse(
        UUID id,
        Long tenantId,
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        LocalDate rateDate) {

    public static ExchangeRateResponse fromDomain(CurrencyExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.id(),
                exchangeRate.tenantId(),
                exchangeRate.fromCurrency().value(),
                exchangeRate.toCurrency().value(),
                exchangeRate.rate(),
                exchangeRate.rateDate());
    }
}
