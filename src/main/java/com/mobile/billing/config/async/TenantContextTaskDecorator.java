package com.mobile.billing.config.async;

import org.springframework.core.task.TaskDecorator;

import com.mobile.billing.domain.ports.out.TenantContextPort;

public class TenantContextTaskDecorator implements TaskDecorator {

    private final TenantContextPort tenantContext;

    public TenantContextTaskDecorator(TenantContextPort tenantContext) {
        this.tenantContext = tenantContext;
    }

    @Override
    public Runnable decorate(Runnable task) {
        Long submittedTenantId = tenantContext.getTenantId();
        return () -> {
            Long previousTenantId = tenantContext.getTenantId();
            setTenantId(submittedTenantId);
            try {
                task.run();
            } finally {
                setTenantId(previousTenantId);
            }
        };
    }

    private void setTenantId(Long tenantId) {
        if (tenantId == null) {
            tenantContext.clear();
        } else {
            tenantContext.setTenantId(tenantId);
        }
    }
}
