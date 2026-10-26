package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.Currency;
import java.util.List;

public interface GetActiveCurrenciesUseCase {

    List<Currency> getActiveCurrencies();
}
