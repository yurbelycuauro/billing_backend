package com.mobile.billing.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.InvoicePdfExporterUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;
import com.mobile.billing.infrastructure.adapter.out.consumption.MonthStringToIntConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenerateInvoicePdfService implements GenerateInvoicePdfUseCase {

    private final GetCustomerUseCase getCustomerUseCase;
    private final InvoicePdfExporterUseCase invoicePdfExporterUseCase;
    private final PaymentRepositoryPort paymentRepositoryPort;

    @Override
    public byte[] generateInvoicePdf(GenerateInvoicePdfCommand command) {
        Customer customer = getCustomerUseCase.getCustomerById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + command.clientId()));

        List<InvoiceItem> items = command.items() == null ? List.of() : command.items();
        BigDecimal netSubtotal = items.stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = netSubtotal.multiply(command.pais().getTasaImpuesto());
        BigDecimal totalAmount = netSubtotal.add(taxAmount);
        Payment payment = command.paymentId() == null
                ? null
                : paymentRepositoryPort.getById(command.paymentId()).orElse(null);

        InvoiceResponse invoiceResponse = new InvoiceResponse(
                null,
                customer.getId(),
                "PREVIEW-" + System.currentTimeMillis(),
                command.anioPeriodo(),
                MonthStringToIntConverter.obtenerNombreMes(command.mesPeriodo()),
                InvoiceStatus.DRAFT,
                LocalDateTime.now(),
                items,
                netSubtotal,
                taxAmount,
                totalAmount,
                customer.getBusinessName(),
                customer.getTaxId(),
                command.moneda(),
                command.pais(),
                payment
        );

        return invoicePdfExporterUseCase.generateInvoicePdf(invoiceResponse);
    }
}
