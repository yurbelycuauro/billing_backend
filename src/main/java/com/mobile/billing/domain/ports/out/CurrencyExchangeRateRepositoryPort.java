package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.model.CurrencyCode;
import java.time.LocalDate;
import java.util.Optional;

public interface CurrencyExchangeRateRepositoryPort {

    boolean exists(Long tenantId, CurrencyCode fromCurrency, CurrencyCode toCurrency, LocalDate rateDate);

    CurrencyExchangeRate save(CurrencyExchangeRate exchangeRate);

    Optional<CurrencyExchangeRate> findByTenant(
            Long tenantId,
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            LocalDate rateDate);

    Optional<CurrencyExchangeRate> findGlobal(
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            LocalDate rateDate);
}
