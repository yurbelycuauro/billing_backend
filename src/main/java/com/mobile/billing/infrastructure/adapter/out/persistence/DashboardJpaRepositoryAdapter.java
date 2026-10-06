package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.DashboardSummary;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.InvoiceStatusSummary;
import com.mobile.billing.domain.model.MonthlyBilled;
import com.mobile.billing.domain.model.MonthlyConsumption;
import com.mobile.billing.domain.ports.out.DashboardRepositoryPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DashboardJpaRepositoryAdapter implements DashboardRepositoryPort {

        private final SpringDataInvoiceRepository invoiceRepository;
        private final SpringDataConsumptionDetailRepository consumptionRepository;

        public DashboardJpaRepositoryAdapter(
                        SpringDataInvoiceRepository invoiceRepository,
                        SpringDataConsumptionDetailRepository consumptionRepository) {
                this.invoiceRepository = invoiceRepository;
                this.consumptionRepository = consumptionRepository;
        }

        @Override
        public DashboardSummary findDashboard(Integer year, Integer month) {
                return new DashboardSummary(
                                findMonthlyBilled(year, month),
                                findMonthlyConsumption(year, month),
                                findInvoiceStatuses(year, month));
        }

        private List<MonthlyBilled> findMonthlyBilled(Integer year, Integer month) {
                return invoiceRepository.sumTotalAmountByPeriodGroupedByMonth(year, month).stream()
                                .map(row -> new MonthlyBilled(
                                                ((Number) row[0]).intValue(),
                                                ((Number) row[1]).intValue(),
                                                (BigDecimal) row[2]))
                                .toList();
        }

        private List<MonthlyConsumption> findMonthlyConsumption(Integer year, Integer month) {
                return consumptionRepository.sumByMonth(year, month).stream()
                                .map(row -> new MonthlyConsumption(
                                                (Integer) row[0],
                                                (Integer) row[1],
                                                ((Number) row[2]).longValue(),
                                                (BigDecimal) row[3]))
                                .toList();
        }

        private List<InvoiceStatusSummary> findInvoiceStatuses(Integer year, Integer month) {
                Map<InvoiceStatus, Long> counts = invoiceRepository
                                .countByStatusByPeriod(year, month).stream()
                                .collect(Collectors.toMap(
                                                row -> (InvoiceStatus) row[0],
                                                row -> ((Number) row[1]).longValue()));

                long totalCount = counts.values().stream()
                                .mapToLong(Long::longValue)
                                .sum();

                return Arrays.stream(InvoiceStatus.values())
                                .map(status -> InvoiceStatusSummary.from(
                                                status,
                                                counts.getOrDefault(status, 0L),
                                                totalCount))
                                .toList();
        }
}