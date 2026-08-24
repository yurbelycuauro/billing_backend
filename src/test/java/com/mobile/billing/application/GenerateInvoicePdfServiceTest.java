package com.mobile.billing.application;


import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.InvoiceItem;

import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase.GenerateInvoiceCommand;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.InvoicePdfExporterUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;

class GenerateInvoicePdfServiceTest {

    @Test
    void shouldGeneratePdfFromInvoiceAndCustomer() {
        GetCustomerUseCase getCustomerUseCase = mock(GetCustomerUseCase.class);
        InvoicePdfExporterUseCase pdfExporter = mock(InvoicePdfExporterUseCase.class);
        PaymentRepositoryPort paymentRepositoryPort = mock(PaymentRepositoryPort.class);

        GenerateInvoicePdfService service = new GenerateInvoicePdfService(getCustomerUseCase, pdfExporter, paymentRepositoryPort);

        List<InvoiceItem> items = List.of(new InvoiceItem(null, "Servicio", 1, BigDecimal.TEN, 100L));
        GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand command = new GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand(
            1L, 2026, 6, 1L, Moneda.PEN, Pais.PE, items);
        Customer customer = Customer.create("12345678", "Acme S.A.", "contacto@acme.com");
        byte[] expectedPdf = new byte[] {1, 2, 3};
        when(getCustomerUseCase.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(pdfExporter.generateInvoicePdf(any(InvoiceResponse.class))).thenReturn(expectedPdf);

        byte[] result = service.generateInvoicePdf(command);

        assertSame(expectedPdf, result);
    }
}
