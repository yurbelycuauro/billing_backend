package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetTenantServiceTest {

    private final TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final GetTenantService service = new GetTenantService(tenantRepository, tenantContext);

    @Test
    void returnsTenantFromCurrentContext() {
        Tenant tenant = new Tenant(
                42L, "CL", "Company", "tax-id", new BigDecimal("0.19"), "CLP", "address", "phone", true);
        when(tenantContext.getTenantId()).thenReturn(42L);
        when(tenantRepository.findById(42L)).thenReturn(Optional.of(tenant));

        assertEquals(Optional.of(tenant), service.getCurrentTenant());
        verify(tenantRepository).findById(42L);
    }

    @Test
    void returnsEmptyWhenContextHasNoTenant() {
        when(tenantContext.getTenantId()).thenReturn(null);

        assertTrue(service.getCurrentTenant().isEmpty());
        verify(tenantRepository, never()).findById(org.mockito.ArgumentMatchers.any());
    }
}
