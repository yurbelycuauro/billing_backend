package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase.GenerateInvoiceCommand;
import com.mobile.billing.domain.ports.in.GetExchangeRateUseCase;
import com.mobile.billing.domain.model.Currency;
import com.mobile.billing.domain.model.CurrencyCode;
import com.mobile.billing.domain.model.CurrencyExchangeRate;
import com.mobile.billing.domain.ports.out.CurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantCurrencyRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateInvoiceServiceTest {

    private final InvoiceRepositoryPort invoiceRepository = mock(InvoiceRepositoryPort.class);
    private final TenantContextPort tenantContext = mock(TenantContextPort.class);
    private final TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
    private final CurrencyRepositoryPort currencyRepository = mock(CurrencyRepositoryPort.class);
    private final TenantCurrencyRepositoryPort tenantCurrencyRepository =
            mock(TenantCurrencyRepositoryPort.class);
    private final GetExchangeRateUseCase exchangeRateUseCase = mock(GetExchangeRateUseCase.class);
    private final CreateInvoiceService service = new CreateInvoiceService(
            invoiceRepository,
            tenantContext,
            tenantRepository,
            currencyRepository,
            tenantCurrencyRepository,
            exchangeRateUseCase);

    @Test
    void generateInvoiceUsesTaxRateFromCurrentTenant() {
        when(tenantContext.getTenantId()).thenReturn(42L);
        when(tenantRepository.findById(42L)).thenReturn(Optional.of(new Tenant(
                42L, "CL", "Company", "tax-id", new BigDecimal("0.19"), "CLP", "address", "phone", true)));
        when(currencyRepository.findActiveByCode(new CurrencyCode("CLP")))
                .thenReturn(Optional.of(new Currency(new CurrencyCode("CLP"), "Peso chileno", "$", 0, true)));
        when(tenantCurrencyRepository.findAllowedCurrency(42L, new CurrencyCode("CLP")))
                .thenReturn(Optional.of(new com.mobile.billing.domain.model.TenantCurrency(
                        new Currency(new CurrencyCode("CLP"), "Peso chileno", "$", 0, true), true)));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GenerateInvoiceCommand command = new GenerateInvoiceCommand(
                5L,
                2026,
                9,
                1L,
                "CLP",
                List.of(new InvoiceItem(null, "Service", 1, new BigDecimal("100"), null)));

        Invoice invoice = service.generateInvoice(command);

        verify(tenantRepository).findById(42L);
        assertEquals(0, invoice.getTaxAmount().compareTo(new BigDecimal("19.00")));
        assertEquals(0, invoice.getTotalAmount().compareTo(new BigDecimal("119.00")));
        assertEquals("CLP", invoice.getBaseCurrency());
        assertEquals("CLP", invoice.getMoneda());
        assertEquals(0, invoice.getExchangeRate().compareTo(BigDecimal.ONE.setScale(6)));
        assertEquals(0, invoice.getTotalAmountBase().compareTo(new BigDecimal("119.0000")));

        invoice.addItem(
                new InvoiceItem(null, "Additional service", 1, new BigDecimal("100"), null),
                new BigDecimal("0.19"));

        assertEquals(0, invoice.getTaxAmount().compareTo(new BigDecimal("38.0000")));
        assertEquals(0, invoice.getTotalAmount().compareTo(new BigDecimal("238.0000")));
    }

    @Test
    void generateInvoiceFreezesTenantExchangeRateAndBaseAmounts() {
        when(tenantContext.getTenantId()).thenReturn(42L);
        when(tenantRepository.findById(42L)).thenReturn(Optional.of(new Tenant(
                42L, "CL", "Company", "tax-id", new BigDecimal("0.19"), "CLP", "address", "phone", true)));
        when(currencyRepository.findActiveByCode(new CurrencyCode("CLP")))
                .thenReturn(Optional.of(new Currency(new CurrencyCode("CLP"), "Peso chileno", "$", 0, true)));
        when(currencyRepository.findActiveByCode(new CurrencyCode("USD")))
                .thenReturn(Optional.of(new Currency(new CurrencyCode("USD"), "Dólar", "US$", 2, true)));
        when(tenantCurrencyRepository.findAllowedCurrency(42L, new CurrencyCode("USD")))
                .thenReturn(Optional.of(new com.mobile.billing.domain.model.TenantCurrency(
                        new Currency(new CurrencyCode("USD"), "Dólar", "US$", 2, true), false)));
        when(exchangeRateUseCase.getExchangeRate(
                org.mockito.ArgumentMatchers.eq("USD"),
                org.mockito.ArgumentMatchers.eq("CLP"),
                org.mockito.ArgumentMatchers.any(LocalDate.class)))
                .thenAnswer(invocation -> new CurrencyExchangeRate(
                        UUID.randomUUID(),
                        42L,
                        new CurrencyCode("USD"),
                        new CurrencyCode("CLP"),
                        new BigDecimal("900.123456"),
                        invocation.getArgument(2)));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GenerateInvoiceCommand command = new GenerateInvoiceCommand(
                5L,
                2026,
                9,
                1L,
                "USD",
                List.of(new InvoiceItem(null, "Service", 1, new BigDecimal("100"), null)));

        Invoice invoice = service.generateInvoice(command);

        assertEquals(0, invoice.getExchangeRate().compareTo(new BigDecimal("900.123456")));
        assertEquals("USD", invoice.getMoneda());
        assertEquals(0, invoice.getNetSubtotalBase().compareTo(new BigDecimal("90012.0000")));
        assertEquals(0, invoice.getTaxAmountBase().compareTo(new BigDecimal("17102.0000")));
        assertEquals(0, invoice.getTotalAmountBase().compareTo(new BigDecimal("107114.0000")));
    }
}