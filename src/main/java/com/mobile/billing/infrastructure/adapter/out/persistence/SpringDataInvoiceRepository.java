package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceEntity;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    @Query(value = """
            select i.id
            from InvoiceEntity i
            where i.issueDate between :start and :end
            """,
            countQuery = """
                    select count(i)
                    from InvoiceEntity i
                    where i.issueDate between :start and :end
                    """)
    Page<Long> findInvoiceIdsByIssueDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query(value = """
            select i.id
            from InvoiceEntity i
            where i.customer.id = :customerId
              and i.issueDate between :start and :end
            """,
            countQuery = """
                    select count(i)
                    from InvoiceEntity i
                    where i.customer.id = :customerId
                      and i.issueDate between :start and :end
                    """)
    Page<Long> findInvoiceIdsByCustomerAndIssueDateBetween(
            @Param("customerId") Long customerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

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

    @Query(value = """
            select i.id
            from InvoiceEntity i
            where (:year is null and i.anioPeriodo is null)
               or i.anioPeriodo = :year
            """,
            countQuery = """
                    select count(i)
                    from InvoiceEntity i
                    where (:year is null and i.anioPeriodo is null)
                       or i.anioPeriodo = :year
                    """)
    Page<Long> findInvoiceIdsByAnioPeriodo(@Param("year") Integer year, Pageable pageable);

    @EntityGraph(attributePaths = { "items" })
    @Query("select distinct i from InvoiceEntity i where i.id in :ids")
    List<InvoiceEntity> findWithItemsByIdIn(@Param("ids") Collection<Long> ids);

    @Override
    @EntityGraph(attributePaths = { "items" })
    Optional<InvoiceEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = { "items" })
    @Query("select distinct i from InvoiceEntity i")
    List<InvoiceEntity> findAll();
}
