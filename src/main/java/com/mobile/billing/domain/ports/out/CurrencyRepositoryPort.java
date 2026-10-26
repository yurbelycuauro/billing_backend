package com.mobile.billing.domain.ports.out;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepositoryPort {

    List<Currency> findActive();

    Optional<Currency> findActiveByCode(CurrencyCode code);
}
