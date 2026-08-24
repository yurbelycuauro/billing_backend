package com.mobile.billing.infrastructure.adapter.out.consumption;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;

@Component
public class ExcelConsumtionAdapter {

    private static final Logger logger = Logger.getLogger(ExcelConsumtionAdapter.class.getName());

    /**
     * Lee datos de consumo desde un archivo Excel.
     * 
     * @param anio Año del período
     * @param mes Mes del período (1-12)
     * @param bytes Contenido del archivo Excel en bytes
     * @return Lista de objetos ConsumptionDetail
     */
    public List<ConsumptionDetail> readFromExcel(Integer anio, Integer mes, byte[] bytes) {
        List<ConsumptionDetail> consumptions = new ArrayList<>();

        try {
            String mesStr = String.format("%02d", mes);  // Convierte 1 a "01", 12 a "12"
            List<ExcelConsumptionRow> rows = readExcelRows(bytes);
            
            for (ExcelConsumptionRow row : rows) {
                ConsumptionDetail detail = mapRowToConsumptionDetail(row, anio, mesStr);
                if (detail != null) {
                    consumptions.add(detail);
                }
            }

            logger.info(String.format("Se leyeron %d registros de consumo del Excel", consumptions.size()));

        } catch (Exception e) {
            logger.severe(String.format("Error al leer archivo Excel: %s", e.getMessage()));
            throw new RuntimeException("Error al procesar archivo Excel de consumo", e);
        }

        return consumptions;
    }

    /**
     * Lee las filas del archivo Excel usando EasyExcel.
     */
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

    /**
     * Convierte una fila del Excel a un objeto de dominio ConsumptionDetail.
     */
    private ConsumptionDetail mapRowToConsumptionDetail(ExcelConsumptionRow row, Integer anio, String mes) {
        try {
            return new ConsumptionDetail(
                null,  // El ID será generado por la base de datos
                row.getYear(),
                row.getMonth(),
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
                true,  // billable
                null,  // billableReason
                DataSource.MANUAL_EXCEL
            );
        } catch (IllegalArgumentException e) {
            logger.warning(String.format("Fila ignorada por validación: %s", e.getMessage()));
            return null;
        }
    }

    /**
     * Listener personalizado para acumular datos del Excel.
     */
    private static class ExcelDataListener extends AnalysisEventListener<ExcelConsumptionRow> {
        private final List<ExcelConsumptionRow> data;

        public ExcelDataListener(List<ExcelConsumptionRow> data) {
            this.data = data;
        }

        @Override
        public void invoke(ExcelConsumptionRow row, AnalysisContext context) {
            data.add(row);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Completado
        }

        @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        // Captura errores específicos de conversión de datos por celda
        if (exception instanceof ExcelDataConvertException convertException) {
            
            Integer rowIndex = convertException.getRowIndex();     // Número de fila en el Excel (1-based)
            Integer columnIndex = convertException.getColumnIndex(); // Índice de columna (0, 1, 2...)
            String cellValue = convertException.getCellData().getStringValue(); // Valor que falló

            String errorMessage = String.format(
                        "Error en el Excel -> Fila: %d, Columna (índice): %d. El valor recibido fue: '%s'",
                        rowIndex + 1, columnIndex, cellValue
                );

            logger.severe(errorMessage);

            // Opción A: Lanzar una excepción de negocio amigable indicando el lugar exacto
            throw new IllegalArgumentException(
                String.format("Error en la Fila %d, Columna %d: No se pudo procesar el valor '%s'", 
                        rowIndex + 1, columnIndex, cellValue)
            );
        }

        // Si es otro tipo de error, mantenemos el comportamiento por defecto
        super.onException(exception, context);
    }
    }
}
