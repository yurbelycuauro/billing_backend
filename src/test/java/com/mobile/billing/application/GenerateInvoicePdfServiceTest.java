package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.InvoiceItem;

import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.InvoicePdfExporterUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;

class GenerateInvoicePdfServiceTest {

    @Test
    void shouldGeneratePdfFromInvoiceAndCustomer() {
        GetCustomerUseCase getCustomerUseCase = mock(GetCustomerUseCase.class);
        InvoicePdfExporterUseCase pdfExporter = mock(InvoicePdfExporterUseCase.class);
        PaymentRepositoryPort paymentRepositoryPort = mock(PaymentRepositoryPort.class);
        TenantContextPort tenantContext = mock(TenantContextPort.class);
        TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);

        GenerateInvoicePdfService service = new GenerateInvoicePdfService(getCustomerUseCase, pdfExporter,
                paymentRepositoryPort, tenantContext, tenantRepository);

        List<InvoiceItem> items = List.of(new InvoiceItem(null, "Servicio", 1, BigDecimal.TEN, 100L));
        GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand command = new GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand(
                1L, 2026, 6, 1L, "PEN", items);
        Customer customer = Customer.create("12345678", "Acme S.A.", "contacto@acme.com");
        byte[] expectedPdf = new byte[] { 1, 2, 3 };
        Tenant tenant = new Tenant(
                42L,
                "PE",
                "Company",
                "tax-id",
                new BigDecimal("0.19"),
                "PEN",
                "address",
                "phone",
                "INTERBANK - CTA. CTE. SOLES: 200-3002609027 / CCI: 003-200-003002609027-31",
                true);
        when(tenantContext.getTenantId()).thenReturn(42L);
        when(tenantRepository.findById(42L)).thenReturn(Optional.of(tenant));
        when(getCustomerUseCase.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(pdfExporter.generateInvoicePdf(any(InvoiceResponse.class), any(Tenant.class))).thenReturn(expectedPdf);

        byte[] result = service.generateInvoicePdf(command);

        ArgumentCaptor<InvoiceResponse> captor = ArgumentCaptor.forClass(InvoiceResponse.class);
        ArgumentCaptor<Tenant> tenantCaptor = ArgumentCaptor.forClass(Tenant.class);
        verify(pdfExporter).generateInvoicePdf(captor.capture(), tenantCaptor.capture());
        InvoiceResponse invoiceResponse = captor.getValue();

        assertSame(expectedPdf, result);
        assertSame(tenant, tenantCaptor.getValue());
        assertEquals("INTERBANK - CTA. CTE. SOLES: 200-3002609027 / CCI: 003-200-003002609027-31",
                tenantCaptor.getValue().getBankAccount());
        assertEquals("Acme S.A.", invoiceResponse.customerName());
        assertEquals("12345678", invoiceResponse.taxId());
        assertEquals("Junio", invoiceResponse.mesPeriodo());
        assertEquals(0, invoiceResponse.taxAmount().compareTo(new BigDecimal("1.9000")));
        assertEquals(0, invoiceResponse.totalAmount().compareTo(new BigDecimal("11.9000")));
    }
}
