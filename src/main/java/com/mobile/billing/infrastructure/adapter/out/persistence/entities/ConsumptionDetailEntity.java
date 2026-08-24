package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "consumption_details",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_consumption_unique_record",
        columnNames = {
            "period_year",
            "period_month",
            "client_name",
            "service_name",
            "commercial_name",
            "direction",
            "type",
            "marking",
            "provider",
            "sale_amount"
        }
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_year")
    private Integer periodYear;

    @Column(name = "period_month")
    private Integer periodMonth;

    private String country;

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "commercial_name")
    private String commercialName;

    private String direction;

    private String type;

    private String marking;

    private String provider;

    private Integer traffic;

    @Column(name = "dollar_sale_rate")
    private BigDecimal dollarSaleRate;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "sale_amount")
    private BigDecimal saleAmount;

    @Column(name = "dollar_cost_rate")
    private BigDecimal dollarCostRate;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "cost_amount")
    private BigDecimal costAmount;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    private BigDecimal margin;
    
    private boolean billable;

    @Column(name = "billable_reason")
    private String billableReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source")
    private DataSource dataSource;

    public static ConsumptionDetailEntity fromDomain (ConsumptionDetail detail) {
        if (detail == null) {
            return null;
        }
        return new ConsumptionDetailEntity(
            detail.getId(),
            detail.getPeriodYear(),
            detail.getPeriodMonth(),
            detail.getCountry(),
            detail.getCostCenter(),
            detail.getClientName(),
            detail.getServiceName(),
            detail.getCommercialName(),
            detail.getDirection(),
            detail.getType(),
            detail.getMarking(),
            detail.getProvider(),
            detail.getTraffic().intValue(),  // Convert BigDecimal to Integer
            detail.getDollarSaleRate(),
            detail.getSalePrice(),
            detail.getSaleAmount(),
            detail.getDollarCostRate(),
            detail.getCostPrice(),
            detail.getCostAmount(),
            detail.getTotalCost(),
            detail.getMargin(),
            detail.isBillable(),
            detail.getBillableReason(),
            detail.getDataSource()
        );
        
    }

    public ConsumptionDetail toDomain() {
        return new com.mobile.billing.domain.model.ConsumptionDetail(
            id,
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
            traffic,
            dollarSaleRate,
            salePrice,
            saleAmount,
            dollarCostRate,
            costPrice,
            totalCost,
            margin,
            billable,
            billableReason,
            dataSource
        );
    }
}
