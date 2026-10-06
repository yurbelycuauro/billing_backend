package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase.GenerateInvoiceCommand;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateInvoiceServiceTest {

    private final InvoiceRepositoryPort invoiceRepository = mock(InvoiceRepositoryPort.class);
    private final CustomerRepositoryPort customerRepository = mock(CustomerRepositoryPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
    private final CreateInvoiceService service = new CreateInvoiceService(
            invoiceRepository, customerRepository, tenantContext, tenantRepository);

    @Test
    void generateInvoiceUsesTaxRateFromCurrentTenant() {
        when(tenantContext.getTenantId()).thenReturn(42L);
        when(tenantRepository.findById(42L)).thenReturn(Optional.of(new Tenant(
                42L, "CL", "Company", "tax-id", new BigDecimal("19.00"), "CLP", "address", "phone", true)));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GenerateInvoiceCommand command = new GenerateInvoiceCommand(
                5L,
                2026,
                9,
                1L,
                Moneda.CLP,
                Pais.CL,
                List.of(new InvoiceItem(null, "Service", 1, new BigDecimal("100"), null)));

        Invoice invoice = service.generateInvoice(command);

        verify(tenantRepository).findById(42L);
        assertEquals(0, invoice.getTaxAmount().compareTo(new BigDecimal("19.00")));
        assertEquals(0, invoice.getTotalAmount().compareTo(new BigDecimal("119.00")));
    }
}