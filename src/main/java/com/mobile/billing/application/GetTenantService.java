package com.mobile.billing.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.GetTenantUseCase;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTenantService implements GetTenantUseCase {

    private final TenantRepositoryPort tenantRepository;

    @Override
    public Optional<Tenant> getTenantById(Long tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}