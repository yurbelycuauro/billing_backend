package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.infrastructure.adapter.out.consumption.MonthStringToIntConverter;

public record InvoiceResponse(
        Long id,
        Long customerId,
        String invoiceNumber,
        Integer anioPeriodo,
        String mesPeriodo,
        InvoiceStatus status,
        LocalDateTime issueDate,
        List<InvoiceItem> items,
        BigDecimal netSubtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String customerName,
        String taxId,
        String moneda,
        Payment payment,
        String baseCurrency,
        BigDecimal exchangeRate,
        BigDecimal netSubtotalBase,
        BigDecimal taxAmountBase,
        BigDecimal totalAmountBase) {
    public static InvoiceResponse fromDomain(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getCustomerId(),
                invoice.getInvoiceNumber(),
                invoice.getAnioPeriodo(),
                MonthStringToIntConverter.obtenerNombreMes(invoice.getMesPiriodo()),
                invoice.getStatus(),
                invoice.getIssueDate(),
                invoice.getItems() != null ? new ArrayList<>(invoice.getItems()) : new ArrayList<>(),
                invoice.getNetSubtotal(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                null,
                null,
                invoice.getMoneda(),
                null,
                invoice.getBaseCurrency(),
                invoice.getExchangeRate(),
                invoice.getNetSubtotalBase(),
                invoice.getTaxAmountBase(),
                invoice.getTotalAmountBase());
    }
}
