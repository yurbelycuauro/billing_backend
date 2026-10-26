package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.TenantCurrency;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetTenantCurrenciesServiceTest {

    private final TenantCurrencyRepositoryPort tenantCurrencies = mock(TenantCurrencyRepositoryPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final GetTenantCurrenciesService service =
            new GetTenantCurrenciesService(tenantCurrencies, tenantContext);

    @Test
    void listsOnlyCurrenciesEnabledForCurrentTenant() {
        var currencies = List.of(
                new TenantCurrency(new Currency(new CurrencyCode("CLP"), "Peso chileno", "$", 0, true), true),
                new TenantCurrency(new Currency(new CurrencyCode("USD"), "Dólar", "US$", 2, true), false));
        when(tenantContext.getTenantId()).thenReturn(17L);
        when(tenantCurrencies.findAllowedCurrencies(17L)).thenReturn(currencies);

        assertEquals(currencies, service.getAllowedCurrencies());
        verify(tenantCurrencies).findAllowedCurrencies(17L);
    }
}
