package com.mobile.billing.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.GetInvoiceUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;

class InvoiceControllerTest {

    @Test
    void shouldReturnCustomerNameInInvoiceResponse() {
        GenerateInvoicePdfUseCase generateInvoicePdfUseCase = mock(GenerateInvoicePdfUseCase.class);
        CreateInvoiceUseCase createInvoiceUseCase = mock(CreateInvoiceUseCase.class);
        GetInvoiceUseCase getInvoiceUseCase = mock(GetInvoiceUseCase.class);
        GetCustomerUseCase getCustomerUseCase = mock(GetCustomerUseCase.class);
        InvoiceController controller = new InvoiceController(
                generateInvoicePdfUseCase,
                createInvoiceUseCase,
                getInvoiceUseCase,
                getCustomerUseCase);

        Invoice invoice = new Invoice(
                7L,
                12L,
                "INV-007",
                2026,
                11,
                InvoiceStatus.DRAFT,
                LocalDateTime.now(),
                List.of(),
                new BigDecimal("0.18"),
                1L,
                "PEN");
        Customer customer = Customer.create("12345678901", "Acme S.A.", "contacto@acme.com");
        when(getInvoiceUseCase.getInvoiceById(7L)).thenReturn(invoice);
        when(getCustomerUseCase.getCustomerById(12L)).thenReturn(Optional.of(customer));

        ResponseEntity<InvoiceResponse> response = controller.getInvoiceById(7L);

        assertNotNull(response.getBody());
        assertEquals("Acme S.A.", response.getBody().customerName());
        assertEquals("12345678901", response.getBody().taxId());
    }
}
