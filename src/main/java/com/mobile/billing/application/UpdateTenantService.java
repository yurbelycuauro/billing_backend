package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.UpdateTenantUseCase;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

@Service
public class UpdateTenantService implements UpdateTenantUseCase {

    private final TenantRepositoryPort tenantRepository;

    public UpdateTenantService(TenantRepositoryPort tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Tenant execute(UpdateTenantCommand command) {
        Tenant tenant = tenantRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("El Tenant no existe."));

        tenant.update(
                tenant.getCountryCode(),
                command.companyName(),
                command.taxId(),
                command.taxRate(),
                command.currencyCode(),
                command.address(),
                command.phone(),
                command.bankAccount());
        if (command.active()) {
            tenant.activate();
        } else {
            tenant.deactivate();
        }

        return tenantRepository.save(tenant);
    }
}