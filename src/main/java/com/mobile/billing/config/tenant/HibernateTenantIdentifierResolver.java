package com.mobile.billing.config.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.mobile.billing.infrastructure.adapter.out.persistence.tenant.TenantContextHolder;

public class HibernateTenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

    private static final Long SYSTEM_TENANT_ID = 0L;

    @Override
    public Long resolveCurrentTenantIdentifier() {
        return TenantContextHolder.getCurrentTenant() != null
                ? TenantContextHolder.getCurrentTenant()
                : SYSTEM_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}