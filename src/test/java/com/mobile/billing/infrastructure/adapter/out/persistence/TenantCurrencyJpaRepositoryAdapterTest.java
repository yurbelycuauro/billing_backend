package com.mobile.billing.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.exception.DefaultTenantCurrencyException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CurrencyEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TenantCurrencyJpaRepositoryAdapterTest {

    private final SpringDataTenantCurrencyRepository tenantCurrencies =
            mock(SpringDataTenantCurrencyRepository.class);
    private final SpringDataTenantRepository tenants = mock(SpringDataTenantRepository.class);
    private final SpringDataCurrencyRepository currencies = mock(SpringDataCurrencyRepository.class);
    private final TenantCurrencyJpaRepositoryAdapter adapter =
            new TenantCurrencyJpaRepositoryAdapter(tenantCurrencies, tenants, currencies);

    @Test
    void enablingDefaultClearsOtherDefaultAndPersistsSelection() {
        Long tenantId = 7L;
        when(tenants.findByIdForUpdate(tenantId))
                .thenReturn(Optional.of(new com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity()));
        CurrencyCode code = new CurrencyCode("USD");
        var currency = new CurrencyEntity("USD", "US Dollar", "$", 2, true);
        when(currencies.findByCodeAndActiveTrue("USD")).thenReturn(Optional.of(currency));
        when(tenantCurrencies.findById(new TenantCurrencyId(tenantId, "USD"))).thenReturn(Optional.empty());
        when(tenantCurrencies.save(any(TenantCurrencyEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = adapter.enable(tenantId, code, true);

        assertEquals("USD", result.currency().code().value());
        org.junit.jupiter.api.Assertions.assertTrue(result.isDefault());
        verify(tenantCurrencies).clearOtherDefaultsForTenant(tenantId, "USD");
    }

    @Test
    void cannotDisableDefaultCurrency() {
        when(tenants.findByIdForUpdate(7L))
                .thenReturn(Optional.of(new com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity()));
        TenantCurrencyEntity defaultCurrency = new TenantCurrencyEntity(
                new TenantCurrencyId(7L, "CLP"),
                new com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity(),
                new CurrencyEntity("CLP", "Chilean Peso", "$", 0, true),
                true);
        when(tenantCurrencies.findById(new TenantCurrencyId(7L, "CLP")))
                .thenReturn(Optional.of(defaultCurrency));

        assertThrows(
                DefaultTenantCurrencyException.class,
                () -> adapter.disable(7L, new CurrencyCode("CLP")));
    }
}
