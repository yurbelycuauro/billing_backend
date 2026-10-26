package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.InvoiceStatus;

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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;

@Entity
@Table(name = "invoices", uniqueConstraints = {
    @UniqueConstraint(name = "uk_invoice_tenant_number", columnNames = {"tenant_id", "invoice_number"})
})
@Getter
@Setter
    @ToString(exclude = "items")
    public class InvoiceEntity extends BaseJpaEntity {

        public InvoiceEntity() {
        }

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

    @Column(name = "net_subtotal", nullable = false, precision = 18, scale = 4)
    private BigDecimal netSubtotal;

    @Column(name = "tax_amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "net_subtotal_base", nullable = false, precision = 18, scale = 4)
    private BigDecimal netSubtotalBase;

    @Column(name = "tax_amount_base", nullable = false, precision = 18, scale = 4)
    private BigDecimal taxAmountBase;

    @Column(name = "total_amount_base", nullable = false, precision = 18, scale = 4)
    private BigDecimal totalAmountBase;

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

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    public InvoiceEntity(
            Long id,
            CustomerEntity customer,
            String invoiceNumber,
            Integer anioPeriodo,
            Integer mesPiriodo,
            BigDecimal netSubtotal,
            BigDecimal taxAmount,
            BigDecimal totalAmount,
            String baseCurrency,
            BigDecimal exchangeRate,
            BigDecimal netSubtotalBase,
            BigDecimal taxAmountBase,
            BigDecimal totalAmountBase,
            InvoiceStatus status,
            LocalDateTime issueDate,
            List<InvoiceItemEntity> items,
            PaymentEntity payment,
            String moneda) {
        this.id = id;
        this.customer = customer;
        this.invoiceNumber = invoiceNumber;
        this.anioPeriodo = anioPeriodo;
        this.mesPiriodo = mesPiriodo;
        this.netSubtotal = netSubtotal;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.baseCurrency = baseCurrency;
        this.exchangeRate = exchangeRate;
        this.netSubtotalBase = netSubtotalBase;
        this.taxAmountBase = taxAmountBase;
        this.totalAmountBase = totalAmountBase;
        this.status = status;
        this.issueDate = issueDate;
        this.items = items;
        this.payment = payment;
        this.moneda = moneda;
    }

    public static InvoiceEntity fromDomain(Invoice invoice, CustomerEntity customer, PaymentEntity payment) {
        if (invoice == null) {
            return null;
        }
        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(invoice.getId());
        entity.setCustomer(customer);
        entity.setInvoiceNumber(invoice.getInvoiceNumber());
        entity.setAnioPeriodo(invoice.getAnioPeriodo());
        entity.setMesPiriodo(invoice.getMesPiriodo());
        entity.setNetSubtotal(invoice.getNetSubtotal());
        entity.setTaxAmount(invoice.getTaxAmount());
        entity.setTotalAmount(invoice.getTotalAmount());
        entity.setBaseCurrency(invoice.getBaseCurrency());
        entity.setExchangeRate(invoice.getExchangeRate());
        entity.setNetSubtotalBase(invoice.getNetSubtotalBase());
        entity.setTaxAmountBase(invoice.getTaxAmountBase());
        entity.setTotalAmountBase(invoice.getTotalAmountBase());
        entity.setStatus(invoice.getStatus());
        entity.setIssueDate(invoice.getIssueDate());
        entity.setPayment(payment);
        entity.setMoneda(invoice.getMoneda());
        entity.setTenantId(invoice.getTenantId());

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
        return Invoice.restore(
            id,
            getTenantId(),
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
            baseCurrency,
            netSubtotal,
            taxAmount,
            totalAmount,
            exchangeRate,
            netSubtotalBase,
            taxAmountBase,
            totalAmountBase
        );
    }
}
