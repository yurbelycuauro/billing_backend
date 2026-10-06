package com.mobile.billing.domain.ports.in;

import java.util.List;
import java.util.Optional;


import com.mobile.billing.domain.model.Tenant;

public interface GetTenantUseCase {
    
    Optional<Tenant> getTenantById(Long tenantId);
    List<Tenant> getAllTenants();

}
