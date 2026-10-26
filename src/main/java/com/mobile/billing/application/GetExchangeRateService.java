package com.mobile.billing.application;

import com.mobile.billing.domain.exception.ExchangeRateNotFoundException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.in.GetExchangeRateUseCase;
import com.mobile.billing.domain.ports.out.CurrencyExchangeRateRepositoryPort;
import com.mobile.billing.domain.ports.out.ExternalExchangeRatePort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class GetExchangeRateService implements GetExchangeRateUseCase {

    private final CurrencyExchangeRateRepositoryPort exchangeRates;
    private final ExternalExchangeRatePort externalExchangeRates;
    private final TenantContextPort tenantContext;

    public GetExchangeRateService(
            CurrencyExchangeRateRepositoryPort exchangeRates,
            ExternalExchangeRatePort externalExchangeRates,
            TenantContextPort tenantContext) {
        this.exchangeRates = exchangeRates;
        this.externalExchangeRates = externalExchangeRates;
        this.tenantContext = tenantContext;
    }

    @Override
    public CurrencyExchangeRate getExchangeRate(String from, String to, LocalDate rateDate) {
        CurrencyCode fromCurrency = new CurrencyCode(from);
        CurrencyCode toCurrency = new CurrencyCode(to);
        Long tenantId = tenantContext.getTenantId();

        if (tenantId != null && tenantId > 0) {
            var tenantRate = exchangeRates.findByTenant(tenantId, fromCurrency, toCurrency, rateDate);
            if (tenantRate.isPresent()) {
                return tenantRate.get();
            }
        }

        var globalRate = exchangeRates.findGlobal(fromCurrency, toCurrency, rateDate);
        if (globalRate.isPresent()) {
            return globalRate.get();
        }

        return externalExchangeRates.fetchRate(fromCurrency.value(), toCurrency.value(), rateDate)
                .map(rate -> exchangeRates.save(new CurrencyExchangeRate(
                        UUID.randomUUID(),
                        null,
                        fromCurrency,
                        toCurrency,
                        rate,
                        rateDate)))
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        fromCurrency.value(), toCurrency.value(), String.valueOf(rateDate)));
    }
}
