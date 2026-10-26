package com.mobile.billing.domain.ports.out;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;

public interface ExternalExchangeRatePort {
    Optional<BigDecimal> fetchRate(String fromCurrency, String toCurrency, LocalDate date);
}
