package com.mobile.billing.config.async;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.ports.out.TenantContextPort;

class TenantContextTaskDecoratorTest {

    @Test
    void shouldPropagateSubmittedTenantAndRestoreWorkerContextAfterFailure() {
        TestTenantContext tenantContext = new TestTenantContext();
        tenantContext.setTenantId(42L);
        TenantContextTaskDecorator decorator = new TenantContextTaskDecorator(tenantContext);
        RuntimeException failure = new RuntimeException("Processing failed");
        Runnable task = decorator.decorate(() -> {
            assertEquals(42L, tenantContext.getTenantId());
            throw failure;
        });

        tenantContext.setTenantId(99L);

        assertThrows(RuntimeException.class, task::run);
        assertEquals(99L, tenantContext.getTenantId());
    }

    @Test
    void shouldClearWorkerContextWhenNoTenantWasSubmitted() {
        TestTenantContext tenantContext = new TestTenantContext();
        TenantContextTaskDecorator decorator = new TenantContextTaskDecorator(tenantContext);
        Runnable task = decorator.decorate(() -> assertNull(tenantContext.getTenantId()));

        tenantContext.setTenantId(99L);

        task.run();

        assertEquals(99L, tenantContext.getTenantId());
    }

    private static class TestTenantContext implements TenantContextPort {
        private final ThreadLocal<Long> tenantId = new ThreadLocal<>();

        @Override
        public void setTenantId(Long tenantId) {
            this.tenantId.set(tenantId);
        }

        @Override
        public Long getTenantId() {
            return tenantId.get();
        }

        @Override
        public void clear() {
            tenantId.remove();
        }
    }
}
