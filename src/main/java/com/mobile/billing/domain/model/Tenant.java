package com.mobile.billing.domain.model;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class Tenant {

    private final Long id;
    private String countryCode;
    private String companyName;
    private String taxId; // RUT, RFC, NIT
    private BigDecimal taxRate; // Porcentaje de impuesto (ej. 19.00)
    private String currencyCode; // ISO 4217 ('CLP', 'MXN', 'USD')
    private String address; // Dirección fiscal del emisor
    private String phone; // Número de teléfono
    private String bankAccount;
    private boolean active; // Indica si el tenant está activo

    public Tenant(Long id,
            String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            boolean active) {
        this(id, countryCode, companyName, taxId, taxRate, currencyCode, address, phone, null, active);
    }

    public Tenant(Long id,
            String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            String bankAccount,
            boolean active) {
        this.id = id;
        this.countryCode = countryCode;
        this.companyName = companyName;
        this.taxId = taxId;
        this.taxRate = taxRate;
        this.currencyCode = currencyCode;
        this.address = address;
        this.phone = phone;
        this.bankAccount = bankAccount;
        this.active = active;
    }

    public static Tenant create(String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone) {
        return create(countryCode, companyName, taxId, taxRate, currencyCode, address, phone, true);
    }

    public static Tenant create(String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            String bankAccount,
            boolean active) {
        return new Tenant(null, countryCode, companyName, taxId, taxRate, currencyCode, address, phone, bankAccount, active);
    }

    public static Tenant create(String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            boolean active) {
        return new Tenant(null, countryCode, companyName, taxId, taxRate, currencyCode, address, phone, active);
    }

    public void update(String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone) {
        this.countryCode = countryCode;
        this.companyName = companyName;
        this.taxId = taxId;
        this.taxRate = taxRate;
        this.currencyCode = currencyCode;
        this.address = address;
        this.phone = phone;
    }

    public void update(String countryCode,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            String bankAccount) {
        update(countryCode, companyName, taxId, taxRate, currencyCode, address, phone);
        this.bankAccount = bankAccount;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

}