package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.TenantCurrency;
import java.util.List;

public interface GetTenantCurrenciesUseCase {

    List<TenantCurrency> getAllowedCurrencies();
}
