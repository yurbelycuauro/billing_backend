package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.out.CurrencyExchangeRateRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CurrencyExchangeRateEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CurrencyExchangeRateJpaRepositoryAdapter implements CurrencyExchangeRateRepositoryPort {

    private final SpringDataCurrencyExchangeRateRepository exchangeRates;
    private final SpringDataCurrencyRepository currencies;

    public CurrencyExchangeRateJpaRepositoryAdapter(
            SpringDataCurrencyExchangeRateRepository exchangeRates,
            SpringDataCurrencyRepository currencies) {
        this.exchangeRates = exchangeRates;
        this.currencies = currencies;
    }

    @Override
    public boolean exists(Long tenantId, CurrencyCode fromCurrency, CurrencyCode toCurrency, LocalDate rateDate) {
        if (tenantId == null) {
            return exchangeRates.existsByTenantIdIsNullAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
                    fromCurrency.value(), toCurrency.value(), rateDate);
        }
        return exchangeRates.existsByTenantIdAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
                tenantId, fromCurrency.value(), toCurrency.value(), rateDate);
    }

    @Override
    @Transactional
    public CurrencyExchangeRate save(CurrencyExchangeRate exchangeRate) {
        var from = currencies.getReferenceById(exchangeRate.fromCurrency().value());
        var to = currencies.getReferenceById(exchangeRate.toCurrency().value());
        CurrencyExchangeRateEntity entity = new CurrencyExchangeRateEntity(
                exchangeRate.id(),
                exchangeRate.tenantId(),
                from,
                to,
                exchangeRate.rate(),
                exchangeRate.rateDate());
        return exchangeRates.save(entity).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrencyExchangeRate> findByTenant(
            Long tenantId,
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            LocalDate rateDate) {
        return exchangeRates.findByTenantIdAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
                        tenantId, fromCurrency.value(), toCurrency.value(), rateDate)
                .map(CurrencyExchangeRateEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrencyExchangeRate> findGlobal(
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            LocalDate rateDate) {
        return exchangeRates.findByTenantIdIsNullAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
                        fromCurrency.value(), toCurrency.value(), rateDate)
                .map(CurrencyExchangeRateEntity::toDomain);
    }
}
