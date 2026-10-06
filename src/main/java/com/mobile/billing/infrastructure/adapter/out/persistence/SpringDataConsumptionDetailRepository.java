package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select c.periodYear, c.periodMonth, coalesce(sum(c.traffic), 0), coalesce(sum(c.saleAmount), 0)
        from ConsumptionDetailEntity c
        where (:year is null or c.periodYear = :year)
          and (:month is null or c.periodMonth <= :month)
        group by c.periodYear, c.periodMonth
        order by c.periodYear, c.periodMonth
        """)
    List<Object[]> sumByMonth(@Param("year") Integer year, @Param("month") Integer month);
}
 