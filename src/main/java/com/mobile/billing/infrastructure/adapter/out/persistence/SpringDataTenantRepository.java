package com.mobile.billing.infrastructure.adapter.out.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity;

public interface SpringDataTenantRepository extends JpaRepository<TenantEntity, Long>  {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tenant from TenantEntity tenant where tenant.id = :tenantId")
    java.util.Optional<TenantEntity> findByIdForUpdate(@Param("tenantId") Long tenantId);
}
