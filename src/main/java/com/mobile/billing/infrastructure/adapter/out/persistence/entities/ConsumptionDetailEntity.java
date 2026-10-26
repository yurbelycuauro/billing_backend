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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "consumption_details", uniqueConstraints = @UniqueConstraint(name = "uk_consumption_unique_record", columnNames = {
        "period_year",
        "period_month",
        "tenant_id",
        "client_name",
        "service_name",
        "commercial_name",
        "direction",
        "type",
        "marking",
        "provider",
        "sale_amount"
}))
@Getter
@Setter
public class ConsumptionDetailEntity extends BaseJpaEntity {

    public ConsumptionDetailEntity() {
    }

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

    @Column(name = "sale_price", precision = 12, scale = 3)
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

    public ConsumptionDetailEntity(
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
            BigDecimal costAmount,
            BigDecimal totalCost,
            BigDecimal margin,
            boolean billable,
            String billableReason,
            DataSource dataSource) {
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
        this.costAmount = costAmount;
        this.totalCost = totalCost;
        this.margin = margin;
        this.billable = billable;
        this.billableReason = billableReason;
        this.dataSource = dataSource;
    }

    public static ConsumptionDetailEntity fromDomain(ConsumptionDetail detail) {
        if (detail == null) {
            return null;
        }
        ConsumptionDetailEntity entity = new ConsumptionDetailEntity(
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
                detail.getTraffic().intValue(), // Convert BigDecimal to Integer
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
                detail.getDataSource());
        entity.setTenantId(detail.getTenantId());
        return entity;

    }

    public ConsumptionDetail toDomain() {
        return new com.mobile.billing.domain.model.ConsumptionDetail(
                id,
                getTenantId(),
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
                dataSource);
    }
}
