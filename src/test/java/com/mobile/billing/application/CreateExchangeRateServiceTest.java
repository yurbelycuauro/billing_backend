package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.in.CreateExchangeRateUseCase.CreateExchangeRateCommand;
import com.mobile.billing.domain.ports.out.CurrencyExchangeRateRepositoryPort;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateExchangeRateServiceTest {

    private final CurrencyRepositoryPort currencies = mock(CurrencyRepositoryPort.class);
    private final CurrencyExchangeRateRepositoryPort exchangeRates =
            mock(CurrencyExchangeRateRepositoryPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final CreateExchangeRateService service =
            new CreateExchangeRateService(currencies, exchangeRates, tenantContext);

    @Test
    void tenantRateUsesTenantFromTrustedContext() {
        when(tenantContext.getTenantId()).thenReturn(17L);
        allowCurrencies("USD", "CLP");
        when(exchangeRates.exists(
                17L, new CurrencyCode("USD"), new CurrencyCode("CLP"), LocalDate.of(2026, 10, 6)))
                .thenReturn(false);
        when(exchangeRates.save(org.mockito.ArgumentMatchers.any(CurrencyExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CurrencyExchangeRate result = service.createForCurrentTenant(command());

        assertEquals(17L, result.tenantId());
        verify(exchangeRates).save(org.mockito.ArgumentMatchers.any(CurrencyExchangeRate.class));
    }

    @Test
    void globalRateIsStoredWithoutTenantId() {
        allowCurrencies("USD", "CLP");
        when(exchangeRates.exists(
                null, new CurrencyCode("USD"), new CurrencyCode("CLP"), LocalDate.of(2026, 10, 6)))
                .thenReturn(false);
        when(exchangeRates.save(org.mockito.ArgumentMatchers.any(CurrencyExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CurrencyExchangeRate result = service.createGlobal(command());

        assertNull(result.tenantId());
        verify(exchangeRates).save(org.mockito.ArgumentMatchers.any(CurrencyExchangeRate.class));
    }

    private void allowCurrencies(String from, String to) {
        when(currencies.findActiveByCode(new CurrencyCode(from)))
                .thenReturn(Optional.of(new Currency(new CurrencyCode(from), from, "$", 2, true)));
        when(currencies.findActiveByCode(new CurrencyCode(to)))
                .thenReturn(Optional.of(new Currency(new CurrencyCode(to), to, "$", 2, true)));
    }

    private CreateExchangeRateCommand command() {
        return new CreateExchangeRateCommand(
                "USD", "CLP", new BigDecimal("912.123456"), LocalDate.of(2026, 10, 6));
    }
}
