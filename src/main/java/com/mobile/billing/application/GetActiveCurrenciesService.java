package com.mobile.billing.application;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.ports.in.GetActiveCurrenciesUseCase;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetActiveCurrenciesService implements GetActiveCurrenciesUseCase {

    private final CurrencyRepositoryPort currencies;

    public GetActiveCurrenciesService(CurrencyRepositoryPort currencies) {
        this.currencies = currencies;
    }

    @Override
    public List<Currency> getActiveCurrencies() {
        return List.copyOf(currencies.findActive());
    }
}
