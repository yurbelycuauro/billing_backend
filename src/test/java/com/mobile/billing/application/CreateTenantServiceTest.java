package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateTenantUseCase.CreateTenantCommand;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateTenantServiceTest {

    private final TenantRepositoryPort tenants = mock(TenantRepositoryPort.class);
    private final CurrencyRepositoryPort currencies = mock(CurrencyRepositoryPort.class);
    private final TenantCurrencyRepositoryPort tenantCurrencies = mock(TenantCurrencyRepositoryPort.class);
    private final CreateTenantService service = new CreateTenantService(tenants, currencies, tenantCurrencies);

    @Test
    void enablesTenantBaseCurrencyAsDefaultOnCreation() {
        var command = new CreateTenantCommand(
                "CL",
                "Example company",
                "tax-id",
                new BigDecimal("0.19"),
                "clp",
                "address",
                "phone",
                true,
                null);
        when(currencies.findActiveByCode(new CurrencyCode("CLP")))
                .thenReturn(Optional.of(new Currency(new CurrencyCode("CLP"), "Peso chileno", "$", 0, true)));
        when(tenants.save(org.mockito.ArgumentMatchers.any(Tenant.class)))
                .thenAnswer(invocation -> {
                    Tenant tenant = invocation.getArgument(0);
                    return new Tenant(
                            42L,
                            tenant.getCountryCode(),
                            tenant.getCompanyName(),
                            tenant.getTaxId(),
                            tenant.getTaxRate(),
                            tenant.getCurrencyCode(),
                            tenant.getAddress(),
                            tenant.getPhone(),
                            tenant.getBankAccount(),
                            tenant.isActive());
                });

        assertEquals(42L, service.createTenant(command));
        verify(tenantCurrencies).enable(42L, new CurrencyCode("CLP"), true);
    }
}
