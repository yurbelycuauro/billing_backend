package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceEntity;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    Page<InvoiceEntity> findByIssueDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<InvoiceEntity> findByCustomer_IdAndIssueDateBetween(Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

        @Query("""
            select i.anioPeriodo, i.mesPiriodo, coalesce(sum(i.totalAmount), 0)
            from InvoiceEntity i
            where (:year is null or i.anioPeriodo = :year)
              and (:month is null or i.mesPiriodo <= :month)
              and i.anioPeriodo is not null
              and i.mesPiriodo is not null
            group by i.anioPeriodo, i.mesPiriodo
            order by i.anioPeriodo, i.mesPiriodo
            """)
        List<Object[]> sumTotalAmountByPeriodGroupedByMonth(
            @Param("year") Integer year,
            @Param("month") Integer month);

    @Query("""
                select i.status, count(i)
                from InvoiceEntity i
            where (:year is null or i.anioPeriodo = :year)
                and (:month is null or i.mesPiriodo <= :month)
                group by i.status
                """)
    List<Object[]> countByStatusByPeriod(
            @Param("year") Integer year,
            @Param("month") Integer month);

    Page<InvoiceEntity> findByAnioPeriodo(Integer year, Pageable pageable);
}
