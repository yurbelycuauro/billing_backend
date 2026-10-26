package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currencies")
public class CurrencyEntity {

    @Id
    @Column(name = "code", length = 3, nullable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "decimal_digits", nullable = false)
    private int decimalDigits;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected CurrencyEntity() {
    }

    public CurrencyEntity(String code, String name, String symbol, int decimalDigits, boolean active) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.decimalDigits = decimalDigits;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public boolean isActive() {
        return active;
    }

    public Currency toDomain() {
        return new Currency(new CurrencyCode(code), name, symbol, decimalDigits, active);
    }
}
