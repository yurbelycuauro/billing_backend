package com.mobile.billing.infrastructure.adapter.out.consumption;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;

@Component
public class ExcelConsumtionAdapter {

    private static final Logger logger = Logger.getLogger(ExcelConsumtionAdapter.class.getName());

    public List<ConsumptionDetail> readFromExcel(Integer anio, Integer mes, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Para origen MANUAL_EXCEL se requiere el archivo de datos.");
        }

        try {
            String mesStr = String.format("%02d", mes);
            List<ExcelConsumptionRow> rows = readExcelRows(bytes);
            List<ConsumptionDetail> consumptions = new ArrayList<>();

            for (ExcelConsumptionRow row : rows) {
                if (row == null) {
                    continue;
                }

                ConsumptionDetail detail = mapRowToConsumptionDetail(row, anio, mesStr);
                if (detail != null) {
                    consumptions.add(detail);
                }
            }

            logger.info(String.format("Se leyeron %d registros de consumo del Excel", consumptions.size()));
            return consumptions;
        } catch (Exception e) {
            logger.severe(String.format("Error al leer archivo Excel: %s", e.getMessage()));
            throw new RuntimeException("Error al procesar archivo Excel de consumo", e);
        }
    }

    private List<ExcelConsumptionRow> readExcelRows(byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        List<ExcelConsumptionRow> rows = new ArrayList<>();
        ExcelDataListener listener = new ExcelDataListener(rows);

        EasyExcel.read(stream, ExcelConsumptionRow.class, listener)
                .sheet(0)
                .headRowNumber(2)
                .doRead();

        return rows;
    }

    private static class ExcelDataListener extends AnalysisEventListener<ExcelConsumptionRow> {
        private final List<ExcelConsumptionRow> rows;

        ExcelDataListener(List<ExcelConsumptionRow> rows) {
            this.rows = rows;
        }

        @Override
        public void invoke(ExcelConsumptionRow row, AnalysisContext context) {
            if (row != null) {
                rows.add(row);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // no-op
        }
    }

    private ConsumptionDetail mapRowToConsumptionDetail(ExcelConsumptionRow row, Integer anio, String mes) {
        try {
            return new ConsumptionDetail(
                    null,
                    Objects.requireNonNullElse(row.getYear(), anio),
                    Objects.requireNonNullElse(row.getMonth(), Integer.parseInt(mes)),
                    row.getCountry(),
                    row.getCostCenter(),
                    row.getClient(),
                    row.getService(),
                    row.getCommercial(),
                    row.getDirection(),
                    row.getType(),
                    row.getMarking(),
                    row.getProvider(),
                    row.getTraffic(),
                    row.getDollarSaleRate(),
                    row.getSalePrice(),
                    row.getSaleAmount(),
                    row.getDollarCostRate(),
                    row.getCostPrice(),
                    row.getTotalCost(),
                    row.getMargin(),
                    true,
                    null,
                    DataSource.MANUAL_EXCEL
            );
        } catch (IllegalArgumentException e) {
            logger.warning(String.format("Fila ignorada por validación: %s", e.getMessage()));
            return null;
        }
    }
}
