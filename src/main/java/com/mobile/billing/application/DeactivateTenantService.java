package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.DeactivateTenantUseCase;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

@Service
public class DeactivateTenantService implements DeactivateTenantUseCase {

    private final TenantRepositoryPort tenantRepository;

    public DeactivateTenantService(TenantRepositoryPort tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void execute(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("El Tenant no existe."));
        tenant.deactivate();
        tenantRepository.save(tenant);
    }
}