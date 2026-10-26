package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.TenantCurrency;

public interface EnableTenantCurrencyUseCase {

    TenantCurrency enableCurrency(String currencyCode, boolean makeDefault);
}
