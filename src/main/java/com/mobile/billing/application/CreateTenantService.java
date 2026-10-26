package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.ports.in.CreateTenantUseCase;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;

import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantService implements CreateTenantUseCase {

    private final TenantRepositoryPort tenantRepository;
    private final CurrencyRepositoryPort currencyRepository;
    private final TenantCurrencyRepositoryPort tenantCurrencyRepository;

    public CreateTenantService(
            TenantRepositoryPort tenantRepository,
            CurrencyRepositoryPort currencyRepository,
            TenantCurrencyRepositoryPort tenantCurrencyRepository) {
        this.tenantRepository = tenantRepository;
        this.currencyRepository = currencyRepository;
        this.tenantCurrencyRepository = tenantCurrencyRepository;
    }

    @Override
    @Transactional
    public Long createTenant(CreateTenantCommand command) {
        CurrencyCode currencyCode = new CurrencyCode(command.currencyCode());
        currencyRepository.findActiveByCode(currencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException(currencyCode.value()));
        Tenant tenant = Tenant.create(
                command.countryCode(),
                command.companyName(),
                command.taxId(),
                command.taxRate(),
                currencyCode.value(),
                command.address(),
                command.phone(),
                command.bankAccount(),
                command.active());

        Long tenantId = Objects.requireNonNull(tenantRepository.save(tenant).getId());
        tenantCurrencyRepository.enable(tenantId, currencyCode, true);
        return tenantId;
    }
}