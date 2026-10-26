package com.mobile.billing.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Payment;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.InvoicePdfExporterUseCase;
import com.mobile.billing.domain.ports.out.PaymentRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;
import com.mobile.billing.infrastructure.adapter.out.consumption.MonthStringToIntConverter;

@Service
public class GenerateInvoicePdfService implements GenerateInvoicePdfUseCase {

        private final GetCustomerUseCase getCustomerUseCase;
        private final InvoicePdfExporterUseCase invoicePdfExporterUseCase;
        private final PaymentRepositoryPort paymentRepositoryPort;
        private final TenantContextPort tenantContext;
        private final TenantRepositoryPort tenantRepository;

        public GenerateInvoicePdfService(
                        GetCustomerUseCase getCustomerUseCase,
                        InvoicePdfExporterUseCase invoicePdfExporterUseCase,
                        PaymentRepositoryPort paymentRepositoryPort,
                        TenantContextPort tenantContext,
                        TenantRepositoryPort tenantRepository) {
                this.getCustomerUseCase = getCustomerUseCase;
                this.invoicePdfExporterUseCase = invoicePdfExporterUseCase;
                this.paymentRepositoryPort = paymentRepositoryPort;
                this.tenantContext = tenantContext;
                this.tenantRepository = tenantRepository;
        }

        @Override
        public byte[] generateInvoicePdf(GenerateInvoicePdfCommand command) {
                Long tenantId = tenantContext.getTenantId();
                if (tenantId == null) {
                        throw new IllegalStateException("Tenant context is required to generate an invoice PDF.");
                }
                Tenant tenant = tenantRepository.findById(tenantId)
                                .orElseThrow(() -> new IllegalArgumentException("Tenant not found."));
                BigDecimal taxRate = tenant.getTaxRate();
                String documentCurrency = new CurrencyCode(command.moneda()).value();

                Customer customer = getCustomerUseCase.getCustomerById(command.clientId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found with ID: " + command.clientId()));

                List<InvoiceItem> items = command.items() == null ? List.of() : command.items();
                BigDecimal netSubtotal = items.stream()
                                .map(InvoiceItem::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .setScale(4, RoundingMode.HALF_UP);

                BigDecimal taxAmount = netSubtotal.multiply(taxRate).setScale(4, RoundingMode.HALF_UP);
                BigDecimal totalAmount = netSubtotal.add(taxAmount).setScale(4, RoundingMode.HALF_UP);
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
                                documentCurrency,
                                payment,
                                documentCurrency,
                                BigDecimal.ONE,
                                netSubtotal,
                                taxAmount,
                                totalAmount);

                return invoicePdfExporterUseCase.generateInvoicePdf(invoiceResponse, tenant);
        }
}
