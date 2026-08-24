package com.mobile.billing.domain.model;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class InvoiceItem {

    private final Long id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;       // quantity * unitPrice
    private Long consumptionDetailId; // Referencia opcional al origen

    public InvoiceItem(Long id, String description, Integer quantity, BigDecimal unitPrice, Long consumptionDetailId) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Item description cannot be empty.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }

        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.consumptionDetailId = consumptionDetailId;
    }

    public static InvoiceItem fromConsumption(ConsumptionDetail consumption) {
        return new InvoiceItem(
            null,
            consumption.getServiceName(),
            consumption.getTraffic(),
            consumption.getSalePrice(),
            consumption.getId()
        );
    }
}
