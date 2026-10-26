package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;

import com.mobile.billing.domain.model.InvoiceItem;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
public class InvoiceItemEntity extends BaseJpaEntity {

    public InvoiceItemEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = true)
    private InvoiceEntity invoice;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    // Permitimos hasta 3 decimales para precios unitarios fraccionados/mayoristas
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 3)
    private BigDecimal unitPrice;

    // El monto total de la línea siempre debe redondearse estrictamente a 2
    // decimales (moneda oficial)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "consumption_detail_id")
    private Long consumptionDetailId;

    public InvoiceItemEntity(
            Long id,
            InvoiceEntity invoice,
            String description,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal amount,
            Long consumptionDetailId) {
        this.id = id;
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.consumptionDetailId = consumptionDetailId;
    }

    public static InvoiceItemEntity fromDomain(InvoiceItem item, InvoiceEntity invoice) {
        if (item == null) {
            return null;
        }

        // Aseguramos que el unitPrice mantenga sus decimales y el amount se redondee
        // financieramente a 2 decimales
        BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal calculatedAmount = unitPrice
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        InvoiceItemEntity entity = new InvoiceItemEntity(
                item.getId(),
                invoice,
                item.getDescription(),
                item.getQuantity(),
                unitPrice,
                calculatedAmount,
                item.getConsumptionDetailId());

        if (invoice != null) {
            entity.setTenantId(invoice.getTenantId());
        }

        return entity;
    }

    public InvoiceItem toDomain() {
        return new InvoiceItem(
                id,
                description,
                quantity,
                unitPrice,
                consumptionDetailId);
    }

    // Buenas prácticas para Entidades JPA (Evita bugs en colecciones y
    // comparaciones)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InvoiceItemEntity that = (InvoiceItemEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}