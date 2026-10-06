package com.mobile.billing.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort; // <-- Necesario para el nombre
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Genera el constructor para atributos 'private final'
public class CreateInvoiceService implements CreateInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final CustomerRepositoryPort customerRepository; // Puerto para obtener el cliente
    private final TenantContextPort tenantContext;
    private final TenantRepositoryPort tenantRepository;

    @Override
    @Transactional // Garantiza consistencia en la base de datos
    public Invoice generateInvoice(GenerateInvoiceCommand command) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required to generate an invoice.");
        }
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found."));
        BigDecimal taxRate = tenant.getTaxRate().divide(BigDecimal.valueOf(100));

        // 2. Instanciar la entidad de dominio Invoice con los ítems recibidos.
        String generatedInvoiceNumber = "INV-" + System.currentTimeMillis();
        Invoice invoice = new Invoice(
                null,
                command.clientId(),
                generatedInvoiceNumber,
                command.anioPeriodo(),
                command.mesPeriodo(),
                InvoiceStatus.DRAFT,
                LocalDateTime.now(),
                command.items(),
                taxRate,
                command.paymentId(),
                command.moneda(),
                command.pais());

        // 3. Guardar y retornar la factura persistida
        return invoiceRepository.save(invoice);
    }

}