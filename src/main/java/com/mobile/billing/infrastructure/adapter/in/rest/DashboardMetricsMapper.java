package com.mobile.billing.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.mobile.billing.domain.model.DashboardSummary;
import com.mobile.billing.domain.model.MonthlyBilled;
import com.mobile.billing.domain.model.MonthlyConsumption;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.DashboardResponse;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.MetricDTO;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.MetricDataDTO;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.MonthlyChartPointDTO;

@Component
public class DashboardMetricsMapper {

    private static final Locale SPANISH = Locale.forLanguageTag("es");

    public DashboardResponse toResponse(DashboardSummary dashboard) {
        return new DashboardResponse(
                dashboard.totalBilled(),
                dashboard.consumptionByMonth(),
                dashboard.invoicesByStatus(),
                toMetrics(dashboard)
        );
    }

    private List<MetricDTO> toMetrics(DashboardSummary dashboard) {
        List<MonthlyBilled> billed = dashboard.totalBilled().stream()
                .sorted(Comparator.comparing(MonthlyBilled::year).thenComparing(MonthlyBilled::month))
                .toList();
        List<MonthlyConsumption> consumption = dashboard.consumptionByMonth().stream()
                .sorted(Comparator.comparing(MonthlyConsumption::year).thenComparing(MonthlyConsumption::month))
                .toList();

        return List.of(
                createMetric("Total Facturado", billed, MonthlyBilled::amount, "FileText", "green"),
                createMetric("Consumo del Mes", consumption, MonthlyConsumption::amount, "Zap", "blue"),
                createMetric("Tráfico Mensual", consumption,
                        item -> BigDecimal.valueOf(item.traffic()), "Activity", "orange")
        );
    }

    private <T> MetricDTO createMetric(
            String label,
            List<T> values,
            Function<T, BigDecimal> valueExtractor,
            String icon,
            String color
    ) {
        List<MonthlyChartPointDTO> chartData = values.stream()
                .map(item -> toChartPoint(item, valueExtractor))
                .toList();
        BigDecimal currentValue = valueAt(values, values.size() - 1, valueExtractor);
        BigDecimal previousValue = valueAt(values, values.size() - 2, valueExtractor);

        return new MetricDTO(
                new MetricDataDTO(
                        label,
                        formatValue(currentValue),
                        formatChange(currentValue, previousValue),
                        currentValue.compareTo(previousValue) >= 0
                ),
                chartData,
                icon,
                color
        );
    }

    private <T> BigDecimal valueAt(List<T> values, int index, Function<T, BigDecimal> valueExtractor) {
        return index >= 0 && index < values.size()
                ? valueExtractor.apply(values.get(index))
                : BigDecimal.ZERO;
    }

    private <T> MonthlyChartPointDTO toChartPoint(T item, Function<T, BigDecimal> valueExtractor) {
        Integer year = item instanceof MonthlyBilled billed
                ? billed.year()
                : ((MonthlyConsumption) item).year();
        Integer month = item instanceof MonthlyBilled billed
                ? billed.month()
                : ((MonthlyConsumption) item).month();
        return new MonthlyChartPointDTO(monthLabel(year, month), valueExtractor.apply(item).doubleValue());
    }

    private String monthLabel(Integer year, Integer month) {
        String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, SPANISH).replace(".", "");
        return monthName.substring(0, 1).toUpperCase(SPANISH) + monthName.substring(1) + " " + year;
    }

    private String formatValue(BigDecimal value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(SPANISH);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(value);
    }

    private String formatChange(BigDecimal currentValue, BigDecimal previousValue) {
        if (previousValue.signum() == 0) {
            return "0%";
        }
        BigDecimal change = currentValue.subtract(previousValue)
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        NumberFormat formatter = NumberFormat.getNumberInstance(SPANISH);
        formatter.setMaximumFractionDigits(1);
        return (change.signum() >= 0 ? "+" : "") + formatter.format(change) + "%";
    }
}