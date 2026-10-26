package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.TenantCurrency;
import java.util.List;
import java.util.Optional;

public interface TenantCurrencyRepositoryPort {

    List<TenantCurrency> findAllowedCurrencies(Long tenantId);

    Optional<TenantCurrency> findAllowedCurrency(Long tenantId, CurrencyCode currencyCode);

    TenantCurrency enable(Long tenantId, CurrencyCode currencyCode, boolean makeDefault);

    void disable(Long tenantId, CurrencyCode currencyCode);
}
