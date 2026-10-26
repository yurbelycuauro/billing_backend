package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity;

@Repository
public class TenantJpaRepositoryAdapter implements TenantRepositoryPort {
    private final SpringDataTenantRepository springDataTenantRepository;

    public TenantJpaRepositoryAdapter(SpringDataTenantRepository springDataTenantRepository) {
        this.springDataTenantRepository = springDataTenantRepository;
    }
   
    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity entity = TenantEntity.fromDomain(tenant);
        TenantEntity savedEntity = springDataTenantRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Tenant> findById(Long id) {
        return springDataTenantRepository.findById(id).map(TenantEntity::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return springDataTenantRepository.findAll().stream().map(TenantEntity::toDomain).collect(java.util.stream.Collectors.toList());
    }

}
