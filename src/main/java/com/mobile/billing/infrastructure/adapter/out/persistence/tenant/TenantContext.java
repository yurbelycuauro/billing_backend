package com.mobile.billing.infrastructure.adapter.out.persistence.tenant;

import com.mobile.billing.domain.ports.out.TenantContextPort;
import org.springframework.stereotype.Component;

@Component
public final class TenantContext implements TenantContextPort {

    private TenantContext() {
    }

    public Long getTenantId() {
        return TenantContextHolder.getCurrentTenant();
    }

    public void setTenantId(Long tenantId) {
        TenantContextHolder.setCurrentTenant(tenantId);
    }

    public void clear() {
        TenantContextHolder.clear();
    }
}