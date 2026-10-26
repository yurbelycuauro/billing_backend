package com.mobile.billing.application;

import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.TenantCurrency;
import com.mobile.billing.domain.ports.in.EnableTenantCurrencyUseCase;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class EnableTenantCurrencyService implements EnableTenantCurrencyUseCase {

    private final TenantCurrencyRepositoryPort tenantCurrencies;
    private final TenantContextPort tenantContext;

    public EnableTenantCurrencyService(
            TenantCurrencyRepositoryPort tenantCurrencies,
            TenantContextPort tenantContext) {
        this.tenantCurrencies = tenantCurrencies;
        this.tenantContext = tenantContext;
    }

    @Override
    public TenantCurrency enableCurrency(String currencyCode, boolean makeDefault) {
        return tenantCurrencies.enable(requireTenant(), new CurrencyCode(currencyCode), makeDefault);
    }

    private Long requireTenant() {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalStateException("An authenticated tenant is required.");
        }
        return tenantId;
    }
}
