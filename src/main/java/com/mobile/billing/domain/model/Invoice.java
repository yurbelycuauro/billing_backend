package com.mobile.billing.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class Invoice {

    private final Long id;
    private Long tenantId;
    private Long customerId;
    private String invoiceNumber;
    private Integer anioPeriodo;
    private Integer mesPiriodo;
    private BigDecimal netSubtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String baseCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal netSubtotalBase;
    private BigDecimal taxAmountBase;
    private BigDecimal totalAmountBase;
    private InvoiceStatus status;
    private LocalDateTime issueDate;
    private final List<InvoiceItem> items; // <-- Líneas de la factura
    private Long paymentId;
    private final String moneda;

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
        String moneda
    ) {
        this(id, null, customerId, invoiceNumber, anioPeriodo, mesPeriodo, status, issueDate, items, taxRate, paymentId, moneda);
    }

    public Invoice(
        Long id,
        Long tenantId,
        Long customerId,
        String invoiceNumber,
        Integer anioPeriodo,
        Integer mesPeriodo,
        InvoiceStatus status,
        LocalDateTime issueDate,
        List<InvoiceItem> items,
        BigDecimal taxRate,
        Long paymentId,
        String moneda
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.invoiceNumber = invoiceNumber;
        this.anioPeriodo = anioPeriodo;
        this.mesPiriodo = mesPeriodo;
        this.status = status;
        this.issueDate = issueDate;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.paymentId = paymentId;
        this.moneda = moneda != null ? new CurrencyCode(moneda).value() : null;
        calculateTotals(taxRate);
        this.baseCurrency = this.moneda;
        calculateBaseAmounts(BigDecimal.ONE, 4);
    }

    public Invoice(
        Long id,
        Long tenantId,
        Long customerId,
        String invoiceNumber,
        Integer anioPeriodo,
        Integer mesPeriodo,
        InvoiceStatus status,
        LocalDateTime issueDate,
        List<InvoiceItem> items,
        BigDecimal taxRate,
        Long paymentId,
        String moneda,
        String baseCurrency,
        BigDecimal exchangeRate,
        int baseCurrencyDecimalDigits
    ) {
        this(
            id,
            tenantId,
            customerId,
            invoiceNumber,
            anioPeriodo,
            mesPeriodo,
            status,
            issueDate,
            items,
            taxRate,
            paymentId,
            moneda);
        if (baseCurrency == null || !baseCurrency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Base currency must be a three-letter ISO code.");
        }
        this.baseCurrency = baseCurrency;
        calculateBaseAmounts(exchangeRate, baseCurrencyDecimalDigits);
    }

    public void addItem(InvoiceItem item, BigDecimal taxRate) {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot add items to a non-draft invoice.");
        }
        this.items.add(item);
        calculateTotals(taxRate);
    }

    private void calculateTotals(BigDecimal taxRate) {
        this.netSubtotal = items.stream()
            .map(InvoiceItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.netSubtotal = this.netSubtotal.setScale(4, RoundingMode.HALF_UP);
        this.taxAmount = this.netSubtotal.multiply(taxRate).setScale(4, RoundingMode.HALF_UP);
        this.totalAmount = this.netSubtotal.add(this.taxAmount).setScale(4, RoundingMode.HALF_UP);
    }

    private void calculateBaseAmounts(BigDecimal rate, int baseCurrencyDecimalDigits) {
        if (rate == null || rate.signum() <= 0) {
            throw new IllegalArgumentException("Exchange rate must be greater than zero.");
        }
        if (baseCurrencyDecimalDigits < 0 || baseCurrencyDecimalDigits > 4) {
            throw new IllegalArgumentException("Base currency decimal digits must be between zero and four.");
        }
        this.exchangeRate = rate.setScale(6, RoundingMode.HALF_UP);
        if (this.exchangeRate.signum() <= 0) {
            throw new IllegalArgumentException("Exchange rate must be greater than zero at six decimal places.");
        }
        this.netSubtotalBase = convertToBase(netSubtotal, baseCurrencyDecimalDigits);
        this.taxAmountBase = convertToBase(taxAmount, baseCurrencyDecimalDigits);
        this.totalAmountBase = this.netSubtotalBase.add(this.taxAmountBase).setScale(4, RoundingMode.UNNECESSARY);
    }

    private BigDecimal convertToBase(BigDecimal amount, int decimalDigits) {
        return amount.multiply(exchangeRate)
            .setScale(decimalDigits, RoundingMode.HALF_UP)
            .setScale(4, RoundingMode.UNNECESSARY);
    }

    public static Invoice restore(
        Long id,
        Long tenantId,
        Long customerId,
        String invoiceNumber,
        Integer anioPeriodo,
        Integer mesPeriodo,
        InvoiceStatus status,
        LocalDateTime issueDate,
        List<InvoiceItem> items,
        BigDecimal taxRate,
        Long paymentId,
        String moneda,
        String baseCurrency,
        BigDecimal netSubtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        BigDecimal exchangeRate,
        BigDecimal netSubtotalBase,
        BigDecimal taxAmountBase,
        BigDecimal totalAmountBase
    ) {
        Invoice invoice = new Invoice(
            id,
            tenantId,
            customerId,
            invoiceNumber,
            anioPeriodo,
            mesPeriodo,
            status,
            issueDate,
            items,
            taxRate,
            paymentId,
            moneda,
            baseCurrency,
            exchangeRate,
            4);
        invoice.netSubtotal = netSubtotal;
        invoice.taxAmount = taxAmount;
        invoice.totalAmount = totalAmount;
        invoice.netSubtotalBase = netSubtotalBase;
        invoice.taxAmountBase = taxAmountBase;
        invoice.totalAmountBase = totalAmountBase;
        return invoice;
    }

    public List<InvoiceItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
