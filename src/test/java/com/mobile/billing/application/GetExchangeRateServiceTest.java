package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.exception.ExchangeRateNotFoundException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.out.CurrencyExchangeRateRepositoryPort;
import com.mobile.billing.domain.ports.out.ExternalExchangeRatePort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetExchangeRateServiceTest {

    private static final Long TENANT_ID = 17L;
    private static final LocalDate RATE_DATE = LocalDate.of(2026, 10, 6);

    private final CurrencyExchangeRateRepositoryPort exchangeRates =
            mock(CurrencyExchangeRateRepositoryPort.class);
    private final ExternalExchangeRatePort externalExchangeRates =
            mock(ExternalExchangeRatePort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final GetExchangeRateService service =
            new GetExchangeRateService(exchangeRates, externalExchangeRates, tenantContext);

    @Test
    void tenantRateTakesPrecedenceOverGlobalRate() {
        CurrencyExchangeRate tenantRate = rate(TENANT_ID, "900.00");
        when(tenantContext.getTenantId()).thenReturn(TENANT_ID);
        when(exchangeRates.findByTenant(
                TENANT_ID, new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.of(tenantRate));

        CurrencyExchangeRate result = service.getExchangeRate("usd", "clp", RATE_DATE);

        assertEquals(tenantRate, result);
        verifyNoInteractionsAfterTenantHit();
    }

    @Test
    void fallsBackToGlobalRateWhenTenantRateIsMissing() {
        CurrencyExchangeRate globalRate = rate(null, "850.00");
        when(tenantContext.getTenantId()).thenReturn(TENANT_ID);
        when(exchangeRates.findByTenant(
                TENANT_ID, new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.empty());
        when(exchangeRates.findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.of(globalRate));

        CurrencyExchangeRate result = service.getExchangeRate("USD", "CLP", RATE_DATE);

        assertEquals(globalRate, result);
        org.mockito.Mockito.verifyNoInteractions(externalExchangeRates);
        verify(exchangeRates).findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE);
    }

    @Test
    void throwsWhenNeitherTenantNorGlobalRateExists() {
        when(tenantContext.getTenantId()).thenReturn(TENANT_ID);
        when(exchangeRates.findByTenant(
                TENANT_ID, new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.empty());
        when(exchangeRates.findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.empty());
        when(externalExchangeRates.fetchRate("USD", "CLP", RATE_DATE)).thenReturn(Optional.empty());

        assertThrows(
                ExchangeRateNotFoundException.class,
                () -> service.getExchangeRate("USD", "CLP", RATE_DATE));
    }

    @Test
    void savesAndReturnsRateFromExternalProviderWhenNoStoredRateExists() {
        CurrencyExchangeRate externalRate = rate(null, "934.14");
        when(tenantContext.getTenantId()).thenReturn(TENANT_ID);
        when(exchangeRates.findByTenant(
                TENANT_ID, new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.empty());
        when(exchangeRates.findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.empty());
        when(externalExchangeRates.fetchRate("USD", "CLP", RATE_DATE))
                .thenReturn(Optional.of(externalRate.rate()));
        when(exchangeRates.save(org.mockito.ArgumentMatchers.any(CurrencyExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CurrencyExchangeRate result = service.getExchangeRate("USD", "CLP", RATE_DATE);

        assertEquals(new BigDecimal("934.14"), result.rate());
        assertEquals(RATE_DATE, result.rateDate());
        verify(exchangeRates).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.tenantId() == null
                        && saved.fromCurrency().equals(new CurrencyCode("USD"))
                        && saved.toCurrency().equals(new CurrencyCode("CLP"))
                        && saved.rate().compareTo(new BigDecimal("934.14")) == 0
                        && saved.rateDate().equals(RATE_DATE)));
    }

    @Test
    void globalLookupSkipsTenantQueryWhenNoTenantIsPresent() {
        CurrencyExchangeRate globalRate = rate(null, "850.00");
        when(tenantContext.getTenantId()).thenReturn(null);
        when(exchangeRates.findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE))
                .thenReturn(Optional.of(globalRate));

        assertEquals(globalRate, service.getExchangeRate("USD", "CLP", RATE_DATE));
        verify(exchangeRates).findGlobal(
                new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE);
    }

    private void verifyNoInteractionsAfterTenantHit() {
        verify(exchangeRates).findByTenant(
                TENANT_ID, new CurrencyCode("USD"), new CurrencyCode("CLP"), RATE_DATE);
        org.mockito.Mockito.verifyNoMoreInteractions(exchangeRates);
    }

    private CurrencyExchangeRate rate(Long tenantId, String amount) {
        return new CurrencyExchangeRate(
                UUID.randomUUID(),
                tenantId,
                new CurrencyCode("USD"),
                new CurrencyCode("CLP"),
                new BigDecimal(amount),
                RATE_DATE);
    }
}
