package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "items")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(name="anio_periodo")
    private Integer anioPeriodo;

    @Column(name="mes_periodo")
    private Integer mesPiriodo;

    @Column(name = "net_subtotal", nullable = false)
    private BigDecimal netSubtotal;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItemEntity> items = new ArrayList<>();

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
    private PaymentEntity payment;

    @Column(name = "moneda")
    private Moneda moneda;

    @Column(name= "pais")
    private Pais pais;

    

    public static InvoiceEntity fromDomain(Invoice invoice, CustomerEntity customer, PaymentEntity payment) {
        if (invoice == null) {
            return null;
        }
        InvoiceEntity entity = new InvoiceEntity(
            invoice.getId(),
            customer,
            invoice.getInvoiceNumber(),
            invoice.getAnioPeriodo(),
            invoice.getMesPiriodo(),
            invoice.getNetSubtotal(),
            invoice.getTaxAmount(),
            invoice.getTotalAmount(),
            invoice.getStatus(),
            invoice.getIssueDate(),
            new ArrayList<>(),
            payment,
            invoice.getMoneda(),
            invoice.getPais()
        );

        if (invoice.getItems() != null) {
            entity.items = invoice.getItems().stream()
                .map(item -> InvoiceItemEntity.fromDomain(item, entity))
                .collect(Collectors.toList());
        }

        return entity;
    }

    public Invoice toDomain() {
        BigDecimal taxRate = BigDecimal.ZERO;
        if (netSubtotal != null && netSubtotal.signum() != 0 && taxAmount != null) {
            taxRate = taxAmount.divide(netSubtotal, 10, RoundingMode.HALF_UP);
        }
        List<InvoiceItem> domainItems = items.stream()
            .map(InvoiceItemEntity::toDomain)
            .collect(Collectors.toList());

        Long customerId = customer != null ? customer.getId() : null;
        Long paymentId = payment != null ? payment.getId() : null;
        return new Invoice(
            id,
            customerId,
            invoiceNumber,
            anioPeriodo,
            mesPiriodo,
            getStatus(),
            issueDate,
            domainItems,
            taxRate,
            paymentId,
            moneda,
            pais
        );
    }
}
