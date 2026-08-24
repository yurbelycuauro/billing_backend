package com.mobile.billing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class Invoice {

    private final Long id;
    private Long customerId;
    private String invoiceNumber;
    private Integer anioPeriodo;
    private Integer mesPiriodo;
    private BigDecimal netSubtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime issueDate;
    private final List<InvoiceItem> items; // <-- Líneas de la factura
    private Long paymentId;
    private final Moneda moneda; // 👈 Tu Enum directamente aquí
    private final Pais pais;

    public Invoice(
        Long id,
        Long customerId,
        String invoiceNumber,
        Integer anioPeriodo,
        Integer mesPeriodo,
        InvoiceStatus status,
        LocalDateTime issueDate,
        List<InvoiceItem> items,
        BigDecimal taxRate, // ej. 0.18 para 18% IGV
        Long paymentId,
        Moneda moneda, // 👈 Tu Enum directamente aquí
        Pais pais
        
    ) {
        this.id = id;
        this.customerId = customerId;
        this.invoiceNumber = invoiceNumber;
        this.anioPeriodo = anioPeriodo;
        this.mesPiriodo = mesPeriodo;
        this.status = status;
        this.issueDate = issueDate;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.paymentId = paymentId;
        this.moneda = moneda;
        this.pais = pais;
        
        // Recalcula totales automáticamente en función de sus ítems
        calculateTotals(taxRate);
    }

    public void addItem(InvoiceItem item) {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot add items to a non-draft invoice.");
        }
        this.items.add(item);
        calculateTotals(BigDecimal.valueOf(0.18)); // Recalcula con IGV por defecto
    }

    private void calculateTotals(BigDecimal taxRate) {
        this.netSubtotal = items.stream()
            .map(InvoiceItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.taxAmount = this.netSubtotal.multiply(taxRate);
        this.totalAmount = this.netSubtotal.add(this.taxAmount);
    }

    public List<InvoiceItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
