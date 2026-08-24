package com.mobile.billing.infrastructure.adapter.out.consumption;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class MonthStringToIntConverter implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String monthStr = cellData.getStringValue();
        if (monthStr == null || monthStr.isBlank()) {
            return null;
        }

        // Si viene como número en texto ("01", "1")
        if (monthStr.trim().matches("\\d+")) {
            return Integer.parseInt(monthStr.trim());
        }

        // Si viene como texto ("Enero", "JANUARY", "ENERO")
        return switch (monthStr.trim().toUpperCase()) {
            case "ENERO", "JANUARY", "JAN" -> 1;
            case "FEBRERO", "FEBRUARY", "FEB" -> 2;
            case "MARZO", "MARCH", "MAR" -> 3;
            case "ABRIL", "APRIL", "APR" -> 4;
            case "MAYO", "MAY" -> 5;
            case "JUNIO", "JUNE", "JUN" -> 6;
            case "JULIO", "JULY", "JUL" -> 7;
            case "AGOSTO", "AUGUST", "AUG" -> 8;
            case "SEPTIEMBRE", "SEPTEMBER", "SEP" -> 9;
            case "OCTUBRE", "OCTOBER", "OCT" -> 10;
            case "NOVIEMBRE", "NOVEMBER", "NOV" -> 11;
            case "DICIEMBRE", "DECEMBER", "DEC" -> 12;
            default -> throw new IllegalArgumentException("Nombre de mes no válido: " + monthStr);
        };
    }

    public static String obtenerNombreMes(int mesNumero) {
        // Usa Locale.of("es", "ES") en lugar de new Locale(...)
        String mes = Month.of(mesNumero)
                .getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));

        // Convierte la primera letra a mayúscula (ej: "julio" -> "Julio")
        return mes.substring(0, 1).toUpperCase() + mes.substring(1);
    }
}
