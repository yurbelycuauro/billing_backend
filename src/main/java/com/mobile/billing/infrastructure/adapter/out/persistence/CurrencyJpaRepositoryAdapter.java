package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CurrencyJpaRepositoryAdapter implements CurrencyRepositoryPort {

    private final SpringDataCurrencyRepository repository;

    public CurrencyJpaRepositoryAdapter(SpringDataCurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Currency> findActive() {
        return repository.findByActiveTrueOrderByCodeAsc().stream()
                .map(entity -> entity.toDomain())
                .toList();
    }

    @Override
    public Optional<Currency> findActiveByCode(CurrencyCode code) {
        return repository.findByCodeAndActiveTrue(code.value())
                .map(entity -> entity.toDomain());
    }
}
