package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantCurrencyId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataTenantCurrencyRepository
        extends JpaRepository<TenantCurrencyEntity, TenantCurrencyId> {

    List<TenantCurrencyEntity> findAllById_TenantIdAndCurrency_ActiveTrueOrderById_CurrencyCode(Long tenantId);

    java.util.Optional<TenantCurrencyEntity> findById_TenantIdAndId_CurrencyCodeAndCurrency_ActiveTrue(
            Long tenantId, String currencyCode);

    @Modifying
    @Query("update TenantCurrencyEntity tenantCurrency "
            + "set tenantCurrency.isDefault = false "
            + "where tenantCurrency.id.tenantId = :tenantId "
            + "and tenantCurrency.id.currencyCode <> :currencyCode "
            + "and tenantCurrency.isDefault = true")
    int clearOtherDefaultsForTenant(
            @Param("tenantId") Long tenantId,
            @Param("currencyCode") String currencyCode);
}
