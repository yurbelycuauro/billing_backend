package com.mobile.billing.infrastructure.adapter.out.persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity;

public interface SpringDataTenantRepository extends JpaRepository<TenantEntity, Long>  {

}
