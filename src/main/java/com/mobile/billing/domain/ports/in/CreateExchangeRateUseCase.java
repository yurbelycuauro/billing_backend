package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.CurrencyExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface CreateExchangeRateUseCase {

    CurrencyExchangeRate createForCurrentTenant(CreateExchangeRateCommand command);

    CurrencyExchangeRate createGlobal(CreateExchangeRateCommand command);

    record CreateExchangeRateCommand(
            String fromCurrency,
            String toCurrency,
            BigDecimal rate,
            LocalDate rateDate) {
    }
}
