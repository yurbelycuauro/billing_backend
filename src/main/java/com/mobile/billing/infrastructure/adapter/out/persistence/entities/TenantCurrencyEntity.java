package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.TenantCurrency;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tenant_currencies", indexes = {
        @Index(name = "idx_tenant_currencies_tenant_id", columnList = "tenant_id")
})
public class TenantCurrencyEntity {

    @EmbeddedId
    private TenantCurrencyId id;

    @MapsId("tenantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @MapsId("currencyCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_code", referencedColumnName = "code", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TenantCurrencyEntity() {
    }

    public TenantCurrencyEntity(
            TenantCurrencyId id,
            TenantEntity tenant,
            CurrencyEntity currency,
            boolean isDefault) {
        this.id = id;
        this.tenant = tenant;
        this.currency = currency;
        this.isDefault = isDefault;
    }

    @PrePersist
    void setCreatedAtIfMissing() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public TenantCurrencyId getId() {
        return id;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public TenantCurrency toDomain() {
        return new TenantCurrency(currency.toDomain(), isDefault);
    }
}
