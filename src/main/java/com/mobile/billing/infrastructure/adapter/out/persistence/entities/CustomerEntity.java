package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_tenant_tax_id", columnNames = { "tenant_id", "tax_id" }),
        @UniqueConstraint(name = "uk_customer_tenant_business_name", columnNames = { "tenant_id", "business_name" })
})
@Getter
@Setter
public class CustomerEntity extends BaseJpaEntity {

    public CustomerEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tax_id", nullable = true)
    private String taxId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "notification_email", nullable = false)
    private String notificationEmail;

    @Column(nullable = false)
    private boolean active = true;

    public CustomerEntity(
            Long id,
            String taxId,
            String businessName,
            String notificationEmail,
            boolean active) {
        this.id = id;
        this.taxId = taxId;
        this.businessName = businessName;
        this.notificationEmail = notificationEmail;
        this.active = active;
    }

    public static CustomerEntity fromDomain(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerEntity entity = new CustomerEntity(
                customer.getId(),
                customer.getTaxId(),
                customer.getBusinessName(),
                customer.getNotificationEmail(),
                customer.isActive());
        entity.setTenantId(customer.getTenantId());
        return entity;
    }

    public Customer toDomain() {
        return new Customer(
                id,
                getTenantId(),
                taxId,
                businessName,
                notificationEmail,
                active);
    }
}
