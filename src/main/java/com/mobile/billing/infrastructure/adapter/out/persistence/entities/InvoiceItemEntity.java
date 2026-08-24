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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.mobile.billing.domain.model.InvoiceItem;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = true)
    private InvoiceEntity invoice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "consumption_detail_id")
    private Long consumptionDetailId;

    public static InvoiceItemEntity fromDomain(InvoiceItem item, InvoiceEntity invoice) {
        if (item == null) {
            return null;
        }
        return new InvoiceItemEntity(
            item.getId(),
            invoice,
            item.getDescription(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getAmount(),
            item.getConsumptionDetailId()
        );
    }

    public InvoiceItem toDomain() {
        return new InvoiceItem(
            id,
            description,
            quantity,
            unitPrice,
            consumptionDetailId
        );
    }
}
