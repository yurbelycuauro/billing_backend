package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TenantCurrencyId implements Serializable {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    protected TenantCurrencyId() {
    }

    public TenantCurrencyId(Long tenantId, String currencyCode) {
        this.tenantId = tenantId;
        this.currencyCode = currencyCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TenantCurrencyId that)) {
            return false;
        }
        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, currencyCode);
    }
}
