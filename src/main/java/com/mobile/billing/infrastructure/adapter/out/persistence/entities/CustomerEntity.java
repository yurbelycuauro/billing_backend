package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tax_id", nullable = false, unique = true)
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
        return new CustomerEntity(
            customer.getId(),
            customer.getTaxId(),
            customer.getBusinessName(),
            customer.getNotificationEmail(),
            customer.isActive()
        );
    }

    public Customer toDomain() {
        return new Customer(
            id,
            taxId,
            businessName,
            notificationEmail,
            active
        );
    }
}
