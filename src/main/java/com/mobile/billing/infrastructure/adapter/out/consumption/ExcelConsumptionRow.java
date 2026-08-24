package com.mobile.billing.infrastructure.adapter.out.consumption;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para mapear filas del archivo Excel de consumo.
 * Cada atributo corresponde a una columna en el Excel.
 */
@Data
public class ExcelConsumptionRow {

    @ExcelProperty(index = 0)
    private Integer year;

    @ExcelProperty(index = 1, converter = MonthStringToIntConverter.class)
    private Integer month;

    @ExcelProperty(index = 2)
    private String country;

    @ExcelProperty(index = 3)
    private String costCenter;

    @ExcelProperty(index = 4)
    private String client;

    @ExcelProperty(index = 5)
    private String service;

    @ExcelProperty(index = 6)
    private String commercial;

    @ExcelProperty(index = 7)
    private String direction;

    @ExcelProperty(index = 8)
    private String type;

    @ExcelProperty(index = 9)
    private String marking;

    @ExcelProperty(index = 10)
    private String provider;

    @ExcelProperty(index = 11)
    private Integer traffic;

    @ExcelProperty(index = 12)
    private BigDecimal dollarSaleRate;

    @ExcelProperty(index = 13)
    private BigDecimal salePrice;

    @ExcelProperty(index = 14)
    private BigDecimal saleAmount;

    @ExcelProperty(index = 15)
    private BigDecimal dollarCostRate;

    @ExcelProperty(index = 16)
    private BigDecimal costPrice;

   

    @ExcelProperty(index = 17)
    private BigDecimal totalCost;

    @ExcelProperty(index = 18)
    private BigDecimal margin;
}
