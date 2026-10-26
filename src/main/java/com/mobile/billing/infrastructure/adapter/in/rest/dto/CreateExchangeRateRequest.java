package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.ports.in.CreateExchangeRateUseCase.CreateExchangeRateCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExchangeRateRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String fromCurrency,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String toCurrency,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 12, fraction = 6) BigDecimal rate,
        @NotNull LocalDate rateDate) {

    public CreateExchangeRateCommand toCommand() {
        return new CreateExchangeRateCommand(fromCurrency, toCurrency, rate, rateDate);
    }
}
