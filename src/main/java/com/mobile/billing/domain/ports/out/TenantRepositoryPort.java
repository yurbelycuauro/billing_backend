package com.mobile.billing.domain.ports.out;

import java.util.List;
import java.util.Optional;
import com.mobile.billing.domain.model.Tenant;

public interface TenantRepositoryPort {
   
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(Long id);
    List<Tenant> findAll();
    
}
