package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CustomerEntity;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByTaxId(String taxId);
    Optional<CustomerEntity> findByBusinessName(String businessName);

    @Query("""
            select customer
            from CustomerEntity customer
            where customer.tenantId = :tenantId
              and lower(trim(customer.businessName)) = lower(trim(:businessName))
            """)
    Optional<CustomerEntity> findByTenantIdAndBusinessName(
            @Param("tenantId") Long tenantId,
            @Param("businessName") String businessName);
}
