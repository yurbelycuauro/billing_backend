package com.mobile.billing.application;

import com.mobile.billing.domain.ports.in.GetTenantCurrenciesUseCase;
import com.mobile.billing.domain.model.TenantCurrency;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetTenantCurrenciesService implements GetTenantCurrenciesUseCase {

    private final TenantCurrencyRepositoryPort tenantCurrencies;
    private final TenantContextPort tenantContext;

    public GetTenantCurrenciesService(
            TenantCurrencyRepositoryPort tenantCurrencies,
            TenantContextPort tenantContext) {
        this.tenantCurrencies = tenantCurrencies;
        this.tenantContext = tenantContext;
    }

    @Override
    public List<TenantCurrency> getAllowedCurrencies() {
        Long tenantId = requireTenant();
        return List.copyOf(tenantCurrencies.findAllowedCurrencies(tenantId));
    }

    private Long requireTenant() {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            throw new IllegalStateException("An authenticated tenant is required.");
        }
        return tenantId;
    }
}
