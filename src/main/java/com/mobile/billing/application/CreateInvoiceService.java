package com.mobile.billing.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort; // <-- Necesario para el nombre
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Genera el constructor para atributos 'private final'
public class CreateInvoiceService implements CreateInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final CustomerRepositoryPort customerRepository; // Puerto para obtener el cliente
    

    @Override
    @Transactional // Garantiza consistencia en la base de datos
    public Invoice generateInvoice(GenerateInvoiceCommand command) {
        
       
        String nombreCliente = customerRepository.findNameById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + command.clientId()));

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
                BigDecimal.valueOf(0.18),
               command.paymentId(),
               command.moneda(),
               command.pais()
        );

        // 3. Guardar y retornar la factura persistida
        return invoiceRepository.save(invoice);
    }

   
}