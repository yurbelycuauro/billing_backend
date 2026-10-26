package com.mobile.billing.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceStatus;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.exception.TenantCurrencyNotAllowedException;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.in.GetExchangeRateUseCase;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

@Service
public class CreateInvoiceService implements CreateInvoiceUseCase {

        private final InvoiceRepositoryPort invoiceRepository;
        private final TenantContextPort tenantContext;
        private final TenantRepositoryPort tenantRepository;
        private final CurrencyRepositoryPort currencyRepository;
        private final TenantCurrencyRepositoryPort tenantCurrencyRepository;
        private final GetExchangeRateUseCase exchangeRateUseCase;

        public CreateInvoiceService(
                        InvoiceRepositoryPort invoiceRepository,
                        TenantContextPort tenantContext,
                        TenantRepositoryPort tenantRepository,
                        CurrencyRepositoryPort currencyRepository,
                        TenantCurrencyRepositoryPort tenantCurrencyRepository,
                        GetExchangeRateUseCase exchangeRateUseCase) {
                this.invoiceRepository = invoiceRepository;
                this.tenantContext = tenantContext;
                this.tenantRepository = tenantRepository;
                this.currencyRepository = currencyRepository;
                this.tenantCurrencyRepository = tenantCurrencyRepository;
                this.exchangeRateUseCase = exchangeRateUseCase;
        }

        @Override
        @Transactional
        public Invoice generateInvoice(GenerateInvoiceCommand command) {
                // ... (todo tu código del método se queda exactamente igual)
                if (command == null || command.moneda() == null) {
                        throw new IllegalArgumentException("Invoice request and document currency are required.");
                }
                Long tenantId = tenantContext.getTenantId();
                if (tenantId == null) {
                        throw new IllegalStateException("Tenant context is required to generate an invoice.");
                }
                Tenant tenant = tenantRepository.findById(tenantId)
                                .orElseThrow(() -> new IllegalArgumentException("Tenant not found."));
                BigDecimal taxRate = tenant.getTaxRate();
                CurrencyCode documentCurrency = new CurrencyCode(command.moneda());
                CurrencyCode baseCurrency = new CurrencyCode(tenant.getCurrencyCode());
                int baseCurrencyDecimalDigits = currencyRepository.findActiveByCode(baseCurrency)
                                .orElseThrow(() -> new CurrencyNotFoundException(baseCurrency.value()))
                                .decimalDigits();
                currencyRepository.findActiveByCode(documentCurrency)
                                .orElseThrow(() -> new CurrencyNotFoundException(documentCurrency.value()));
                tenantCurrencyRepository.findAllowedCurrency(tenantId, documentCurrency)
                                .orElseThrow(() -> new TenantCurrencyNotAllowedException(documentCurrency.value()));

                LocalDateTime issueDate = LocalDateTime.now();
                BigDecimal exchangeRate = documentCurrency.equals(baseCurrency)
                                ? BigDecimal.ONE
                                : exchangeRateUseCase.getExchangeRate(
                                                documentCurrency.value(),
                                                baseCurrency.value(),
                                                issueDate.toLocalDate()).rate();
                String generatedInvoiceNumber = "INV-" + System.currentTimeMillis();
                Invoice invoice = new Invoice(
                                null,
                                tenantId,
                                command.clientId(),
                                generatedInvoiceNumber,
                                command.anioPeriodo(),
                                command.mesPeriodo(),
                                InvoiceStatus.DRAFT,
                                issueDate,
                                command.items(),
                                taxRate,
                                command.paymentId(),
                                documentCurrency.value(),
                                baseCurrency.value(),
                                exchangeRate,
                                baseCurrencyDecimalDigits);

                return invoiceRepository.save(invoice);
        }
}