package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.CurrencyExchangeRate;
import java.time.LocalDate;

public interface GetExchangeRateUseCase {

    CurrencyExchangeRate getExchangeRate(String fromCurrency, String toCurrency, LocalDate rateDate);
}
