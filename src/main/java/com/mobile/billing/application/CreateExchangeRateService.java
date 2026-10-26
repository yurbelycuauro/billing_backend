package com.mobile.billing.application;

import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.exception.DuplicateExchangeRateException;
import com.mobile.billing.domain.exception.InvalidExchangeRateException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.in.CreateExchangeRateUseCase;
import com.mobile.billing.domain.ports.out.CurrencyExchangeRateRepositoryPort;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateExchangeRateService implements CreateExchangeRateUseCase {

    private final CurrencyRepositoryPort currencies;
    private final CurrencyExchangeRateRepositoryPort exchangeRates;
    private final TenantContextPort tenantContext;

    public CreateExchangeRateService(
            CurrencyRepositoryPort currencies,
            CurrencyExchangeRateRepositoryPort exchangeRates,
            TenantContextPort tenantContext) {
        this.currencies = currencies;
        this.exchangeRates = exchangeRates;
        this.tenantContext = tenantContext;
    }

    @Override
    public CurrencyExchangeRate createForCurrentTenant(CreateExchangeRateCommand command) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalStateException("An authenticated tenant is required to create a tenant exchange rate.");
        }
        return create(command, tenantId);
    }

    @Override
    public CurrencyExchangeRate createGlobal(CreateExchangeRateCommand command) {
        return create(command, null);
    }

    private CurrencyExchangeRate create(CreateExchangeRateCommand command, Long tenantId) {
        if (command == null) {
            throw new InvalidExchangeRateException("Exchange rate request is required.");
        }
        CurrencyCode fromCurrency = new CurrencyCode(command.fromCurrency());
        CurrencyCode toCurrency = new CurrencyCode(command.toCurrency());
        requireActiveCurrency(fromCurrency);
        requireActiveCurrency(toCurrency);
        CurrencyExchangeRate exchangeRate = new CurrencyExchangeRate(
                UUID.randomUUID(),
                tenantId,
                fromCurrency,
                toCurrency,
                command.rate(),
                command.rateDate());

        if (exchangeRates.exists(tenantId, fromCurrency, toCurrency, exchangeRate.rateDate())) {
            throw new DuplicateExchangeRateException();
        }

        return exchangeRates.save(exchangeRate);
    }

    private void requireActiveCurrency(CurrencyCode code) {
        if (currencies.findActiveByCode(code).isEmpty()) {
            throw new CurrencyNotFoundException(code.value());
        }
    }
}
