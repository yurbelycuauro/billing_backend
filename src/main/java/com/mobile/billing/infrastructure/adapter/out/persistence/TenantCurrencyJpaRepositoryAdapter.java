package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.exception.DefaultTenantCurrencyException;
import com.mobile.billing.domain.exception.TenantCurrencyNotFoundException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.TenantCurrency;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TenantCurrencyJpaRepositoryAdapter implements TenantCurrencyRepositoryPort {

    private final SpringDataTenantCurrencyRepository tenantCurrencies;
    private final SpringDataTenantRepository tenants;
    private final SpringDataCurrencyRepository currencies;

    public TenantCurrencyJpaRepositoryAdapter(
            SpringDataTenantCurrencyRepository tenantCurrencies,
            SpringDataTenantRepository tenants,
            SpringDataCurrencyRepository currencies) {
        this.tenantCurrencies = tenantCurrencies;
        this.tenants = tenants;
        this.currencies = currencies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantCurrency> findAllowedCurrencies(Long tenantId) {
        return tenantCurrencies.findAllById_TenantIdAndCurrency_ActiveTrueOrderById_CurrencyCode(tenantId).stream()
                .map(TenantCurrencyEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantCurrency> findAllowedCurrency(Long tenantId, CurrencyCode currencyCode) {
        return tenantCurrencies.findById_TenantIdAndId_CurrencyCodeAndCurrency_ActiveTrue(
                        tenantId, currencyCode.value())
                .map(TenantCurrencyEntity::toDomain);
    }

    @Override
    @Transactional
    public TenantCurrency enable(Long tenantId, CurrencyCode currencyCode, boolean makeDefault) {
        var tenant = tenants.findByIdForUpdate(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        TenantCurrencyId id = new TenantCurrencyId(tenantId, currencyCode.value());
        var currency = currencies.findByCodeAndActiveTrue(currencyCode.value())
                .orElseThrow(() -> new CurrencyNotFoundException(currencyCode.value()));
        TenantCurrencyEntity entity = tenantCurrencies.findById(id)
                .orElseGet(() -> new TenantCurrencyEntity(
                        id,
                        tenant,
                        currency,
                        false));

        if (makeDefault) {
            tenantCurrencies.clearOtherDefaultsForTenant(tenantId, currencyCode.value());
            entity.setDefault(true);
        }
        return tenantCurrencies.save(entity).toDomain();
    }

    @Override
    @Transactional
    public void disable(Long tenantId, CurrencyCode currencyCode) {
        tenants.findByIdForUpdate(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        TenantCurrencyId id = new TenantCurrencyId(tenantId, currencyCode.value());
        TenantCurrencyEntity entity = tenantCurrencies.findById(id)
                .orElseThrow(() -> new TenantCurrencyNotFoundException(currencyCode.value()));
        if (entity.isDefault()) {
            throw new DefaultTenantCurrencyException(currencyCode.value());
        }
        tenantCurrencies.delete(entity);
    }
}
