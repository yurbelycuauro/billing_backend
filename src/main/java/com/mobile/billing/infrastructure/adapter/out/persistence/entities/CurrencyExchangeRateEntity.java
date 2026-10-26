package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "currency_exchange_rates", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_exchange_rate_tenant_pair_date",
                columnNames = {"tenant_id", "from_currency", "to_currency", "rate_date"})
})
public class CurrencyExchangeRateEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "from_currency",
            referencedColumnName = "code",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_exchange_rate_from_currency"))
    private CurrencyEntity fromCurrency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "to_currency",
            referencedColumnName = "code",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_exchange_rate_to_currency"))
    private CurrencyEntity toCurrency;

    @Column(name = "rate", precision = 18, scale = 6, nullable = false)
    private BigDecimal rate;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    protected CurrencyExchangeRateEntity() {
    }

    public CurrencyExchangeRateEntity(
            UUID id,
            Long tenantId,
            CurrencyEntity fromCurrency,
            CurrencyEntity toCurrency,
            BigDecimal rate,
            LocalDate rateDate) {
        this.id = id;
        this.tenantId = tenantId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.rateDate = rateDate;
    }

    public UUID getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public CurrencyEntity getFromCurrency() {
        return fromCurrency;
    }

    public CurrencyEntity getToCurrency() {
        return toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    public CurrencyExchangeRate toDomain() {
        return new CurrencyExchangeRate(
                id,
                tenantId,
                new CurrencyCode(fromCurrency.getCode()),
                new CurrencyCode(toCurrency.getCode()),
                rate,
                rateDate);
    }
}
