package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CurrencyExchangeRateEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCurrencyExchangeRateRepository
        extends JpaRepository<CurrencyExchangeRateEntity, UUID> {

    boolean existsByTenantIdAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
            Long tenantId, String fromCurrency, String toCurrency, LocalDate rateDate);

    boolean existsByTenantIdIsNullAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
            String fromCurrency, String toCurrency, LocalDate rateDate);

    Optional<CurrencyExchangeRateEntity> findByTenantIdAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
            Long tenantId, String fromCurrency, String toCurrency, LocalDate rateDate);

    Optional<CurrencyExchangeRateEntity> findByTenantIdIsNullAndFromCurrency_CodeAndToCurrency_CodeAndRateDate(
            String fromCurrency, String toCurrency, LocalDate rateDate);
}
