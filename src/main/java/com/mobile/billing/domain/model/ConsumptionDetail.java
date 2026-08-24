package com.mobile.billing.domain.model;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ConsumptionDetail {

    private final Long id;
    private Integer periodYear;
    private Integer periodMonth;
    private String country;
    private String costCenter;
    private String clientName;
    private String serviceName;
    private String commercialName;
    private String direction;
    private String type;
    private String marking;
    private String provider;
    private Integer traffic;
    private BigDecimal dollarSaleRate;
    private BigDecimal salePrice;
    private BigDecimal saleAmount;
    private BigDecimal dollarCostRate;
    private BigDecimal costPrice;
    private BigDecimal costAmount;
    private BigDecimal totalCost;
    private BigDecimal margin;
   
    private boolean billable;
    private String billableReason;
    private DataSource dataSource;

    public ConsumptionDetail(
        Long id,
        Integer periodYear,
        Integer periodMonth,
        String country,
        String costCenter,
        String clientName,
        String serviceName,
        String commercialName,
        String direction,
        String type,
        String marking,
        String provider,
        Integer traffic,
        BigDecimal dollarSaleRate,
        BigDecimal salePrice,
        BigDecimal saleAmount,
        BigDecimal dollarCostRate,
        BigDecimal costPrice,
       
        BigDecimal totalCost,
        BigDecimal margin,

        boolean billable,
        String billableReason,
        
       
        DataSource dataSource
    ) {
       // validateInputs(serviceName, messageCount, unitPrice, periodYear, periodMonth, dataSource);
        
        this.id = id;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.country = country;
        this.costCenter = costCenter;
        this.clientName = clientName;
        this.serviceName = serviceName;
        this.commercialName = commercialName;
        this.direction = direction;
        this.type = type;
        this.marking = marking;
        this.provider = provider;
        this.traffic = traffic;
        this.dollarSaleRate = dollarSaleRate;
        this.salePrice = salePrice;
        this.saleAmount = saleAmount;
        this.dollarCostRate = dollarCostRate;
        this.costPrice = costPrice;
      
        this.totalCost = totalCost;
        this.margin = margin;
        this.dataSource = dataSource;
        this.billable = true;
        this.billableReason = null;
    }

    // Método de fábrica simplificado
    public static ConsumptionDetail create(
        Integer periodYear,
        Integer periodMonth,
        String country,
        String costCenter,
        String clientName,
        String serviceName,
        String commercialName,
        String direction,
        String type,
        String marking,
        String provider,
        BigDecimal traffic,
        BigDecimal dollarSaleRate,
        BigDecimal salePrice,
        BigDecimal saleAmount,
        BigDecimal dollarCostRate,
        BigDecimal costPrice,
       
        BigDecimal totalCost,
        BigDecimal margin,
        
       
        DataSource dataSource
    ) {
        return new ConsumptionDetail(
            null,
            periodYear,
            periodMonth,
            country,
            costCenter,
            clientName,
            serviceName,
            commercialName,
            direction,
            type,
            marking,
            provider,
            traffic.intValue(),
            dollarSaleRate,
            salePrice,
            saleAmount,
            dollarCostRate,
            costPrice,
           
            totalCost,
            margin,

           
            true, // billable by default
            null, // no reason for billable by default
           
            dataSource
        );
    }

    // Comportamiento de negocio para eximir de cobro
    public void markAsNonBillable(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A valid reason is required when marking a consumption as non-billable.");
        }
        this.billable = false;
        this.billableReason = reason;
    }

    private void validateInputs(
        String serviceName,
        Integer messageCount,
        BigDecimal unitPrice,
        Integer periodYear,
        Integer periodMonth,
        DataSource dataSource
    ) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name cannot be empty.");
        }
        if (messageCount == null || messageCount < 0) {
            throw new IllegalArgumentException("Message count must be a non-negative integer.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        if (periodYear == null || periodYear < 2000) {
            throw new IllegalArgumentException("Invalid period year.");
        }
        if (periodMonth == null || periodMonth < 1 || periodMonth > 12) {
            throw new IllegalArgumentException("Period month must be a valid month (1-12).");
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source type is required.");
        }
    }
}