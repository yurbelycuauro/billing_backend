package com.mobile.billing.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.GetTenantUseCase;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

@Service
public class GetTenantService implements GetTenantUseCase {

    private final TenantRepositoryPort tenantRepository;
    private final TenantContextPort tenantContext;

    public GetTenantService(TenantRepositoryPort tenantRepository, TenantContextPort tenantContext) {
        this.tenantRepository = tenantRepository;
        this.tenantContext = tenantContext;
    }

    @Override
    public Optional<Tenant> getCurrentTenant() {
        Long tenantId = tenantContext.getTenantId();
        return tenantId == null ? Optional.empty() : tenantRepository.findById(tenantId);
    }

    @Override
    public Optional<Tenant> getTenantById(Long tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}