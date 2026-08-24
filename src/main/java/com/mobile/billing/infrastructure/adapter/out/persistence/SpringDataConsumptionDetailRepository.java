package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.ConsumptionDetailEntity;

public interface SpringDataConsumptionDetailRepository extends JpaRepository<ConsumptionDetailEntity, Long> {
    List<ConsumptionDetailEntity> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth);
    List<ConsumptionDetailEntity> findByPeriodYearAndPeriodMonthAndClientName(Integer periodYear, Integer periodMonth, String clientName);
    boolean existsByPeriodYearAndPeriodMonthAndClientNameAndServiceNameAndCommercialNameAndDirectionAndTypeAndMarkingAndProviderAndSaleAmount(
        Integer periodYear,
        Integer periodMonth,
        String clientName,
        String serviceName,
        String commercialName,
        String direction,
        String type,
        String marking,
        String provider,
        BigDecimal saleAmount
    );
}
 