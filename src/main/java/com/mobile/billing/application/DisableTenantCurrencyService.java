package com.mobile.billing.application;

import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.ports.in.DisableTenantCurrencyUseCase;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DisableTenantCurrencyService implements DisableTenantCurrencyUseCase {

    private final TenantCurrencyRepositoryPort tenantCurrencies;
    private final TenantContextPort tenantContext;

    public DisableTenantCurrencyService(
            TenantCurrencyRepositoryPort tenantCurrencies,
            TenantContextPort tenantContext) {
        this.tenantCurrencies = tenantCurrencies;
        this.tenantContext = tenantContext;
    }

    @Override
    public void disableCurrency(String currencyCode) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalStateException("An authenticated tenant is required.");
        }
        tenantCurrencies.disable(tenantId, new CurrencyCode(currencyCode));
    }
}
