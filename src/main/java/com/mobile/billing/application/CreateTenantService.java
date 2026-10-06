package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateTenantUseCase;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateTenantService implements CreateTenantUseCase {

    private final TenantRepositoryPort tenantRepository;

    @Override
    public Long createTenant(CreateTenantCommand command) {
        Tenant tenant = Tenant.create(
                command.countryCode(),
                command.companyName(),
                command.taxId(),
                command.taxRate(),
                command.currencyCode(),
                command.address(),
                command.phone(),
                command.bankAccount(),
                command.active());

        return tenantRepository.save(tenant).getId();
    }
}