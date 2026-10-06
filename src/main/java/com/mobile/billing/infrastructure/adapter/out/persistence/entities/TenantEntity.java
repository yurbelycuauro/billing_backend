package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import java.math.BigDecimal;

import com.mobile.billing.domain.model.Tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "country_code", nullable = false, unique = true)
    private String countryCode;

    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId; // RUT, RFC, NIT
    @Column(name = "tax_rate", nullable = false)
    private BigDecimal taxRate; // Porcentaje de impuesto (ej. 19.00)
    @Column(name = "currency_code", nullable = false)
    private String currencyCode; // ISO 4217 ('CLP', 'MXN', 'USD')
    @Column(name = "address", nullable = false)
    private String address; // Dirección fiscal del emisor
    @Column(name = "phone", nullable = false)
    private String phone; // Número de teléfono
    @Column(name = "bank_account")
    private String bankAccount;
    @Column(name = "active", nullable = false)
    private boolean active;


    public static TenantEntity fromDomain(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantEntity entity = new TenantEntity();
        entity.setId(tenant.getId());
        entity.setCountryCode(tenant.getCountryCode());
        entity.setCompanyName(tenant.getCompanyName());
        entity.setTaxId(tenant.getTaxId());
        entity.setTaxRate(tenant.getTaxRate());
        entity.setCurrencyCode(tenant.getCurrencyCode());
        entity.setAddress(tenant.getAddress());
        entity.setPhone(tenant.getPhone());
        entity.setBankAccount(tenant.getBankAccount());
        entity.setActive(tenant.isActive());
        return entity;
    }

    public Tenant toDomain() {
        return new Tenant(
            id,
            countryCode,
            companyName,
            taxId,
            taxRate,
            currencyCode,
            address,
            phone,
            bankAccount,
            active
        );
    }

}
