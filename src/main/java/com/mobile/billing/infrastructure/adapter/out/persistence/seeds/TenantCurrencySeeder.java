package com.mobile.billing.infrastructure.adapter.out.persistence.seeds;

import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(3)
@Slf4j
public class TenantCurrencySeeder implements CommandLineRunner {

    private final TenantRepositoryPort tenants;
    private final CurrencyRepositoryPort currencies;
    private final TenantCurrencyRepositoryPort tenantCurrencies;

    public TenantCurrencySeeder(
            TenantRepositoryPort tenants,
            CurrencyRepositoryPort currencies,
            TenantCurrencyRepositoryPort tenantCurrencies) {
        this.tenants = tenants;
        this.currencies = currencies;
        this.tenantCurrencies = tenantCurrencies;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (var tenant : tenants.findAll()) {
            CurrencyCode baseCurrency = new CurrencyCode(tenant.getCurrencyCode());
            try {
                currencies.findActiveByCode(baseCurrency)
                        .orElseThrow(() -> new CurrencyNotFoundException(baseCurrency.value()));
            } catch (CurrencyNotFoundException exception) {
                log.warn(
                        "Tenant base currency is absent or inactive in the global catalog; "
                                + "no default tenant currency was seeded: tenantId={}, currencyCode={}",
                        tenant.getId(),
                        baseCurrency.value());
                continue;
            }
            if (tenantCurrencies.findAllowedCurrency(tenant.getId(), baseCurrency).isEmpty()) {
                tenantCurrencies.enable(tenant.getId(), baseCurrency, true);
            }
        }
    }
}
