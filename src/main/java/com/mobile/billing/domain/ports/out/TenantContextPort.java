package com.mobile.billing.domain.ports.out;

public interface TenantContextPort {
    void setTenantId(Long tenantId);

    Long getTenantId();

    void clear();
}