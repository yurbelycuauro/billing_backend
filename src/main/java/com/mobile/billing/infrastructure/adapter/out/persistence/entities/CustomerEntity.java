package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_tenant_tax_id", columnNames = { "tenant_id", "tax_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity extends BaseJpaEntity {

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
