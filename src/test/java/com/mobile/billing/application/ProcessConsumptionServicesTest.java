package com.mobile.billing.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.DataSource;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase.ProcessConsumptionCommand;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionExtractorPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;

class ProcessConsumptionServicesTest {

    @Test
    void shouldContinueProcessingWhenCustomerAlreadyExistsForTenant() {
        ConsumptionExtractorPort extractor = mock(ConsumptionExtractorPort.class);
        ConsumptionDetailRepositoryPort consumptionRepository = mock(ConsumptionDetailRepositoryPort.class);
        CustomerRepositoryPort customerRepository = mock(CustomerRepositoryPort.class);
        TenantContextPort tenantContext = mock(TenantContextPort.class);
        ProcessConsumptionServices service = new ProcessConsumptionServices(
                extractor, consumptionRepository, customerRepository, tenantContext);
        ProcessConsumptionCommand command = new ProcessConsumptionCommand(DataSource.DIRECT_DB, 2026, 10, null);
        List<ConsumptionDetail> details = List.of(consumptionDetail("Cliente existente"));

        when(tenantContext.getTenantId()).thenReturn(42L);
        when(extractor.extraerConsumptions(DataSource.DIRECT_DB, 2026, 10, null)).thenReturn(details);
        when(customerRepository.findByTenantIdAndBusinessName(42L, "Cliente existente"))
                .thenReturn(Optional.of(Customer.create(42L, null, "Cliente existente", "contacto@example.com")));

        service.processConsumption(command);

        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any(Customer.class));
        verify(consumptionRepository).saveAll(details);
    }

    @Test
    void shouldRegisterNewCustomersAndSaveConsumption() {
        ConsumptionExtractorPort extractor = mock(ConsumptionExtractorPort.class);
        ConsumptionDetailRepositoryPort consumptionRepository = mock(ConsumptionDetailRepositoryPort.class);
        CustomerRepositoryPort customerRepository = mock(CustomerRepositoryPort.class);
        TenantContextPort tenantContext = mock(TenantContextPort.class);
        ProcessConsumptionServices service = new ProcessConsumptionServices(
                extractor, consumptionRepository, customerRepository, tenantContext);
        ProcessConsumptionCommand command = new ProcessConsumptionCommand(DataSource.DIRECT_DB, 2026, 10, null);
        List<ConsumptionDetail> details = List.of(consumptionDetail("Cliente nuevo"));

        when(tenantContext.getTenantId()).thenReturn(42L);
        when(extractor.extraerConsumptions(DataSource.DIRECT_DB, 2026, 10, null)).thenReturn(details);
        when(customerRepository.findByTenantIdAndBusinessName(42L, "Cliente nuevo")).thenReturn(Optional.empty());

        service.processConsumption(command);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Assertions.assertEquals(42L, customerCaptor.getValue().getTenantId());
        Assertions.assertEquals("Cliente nuevo", customerCaptor.getValue().getBusinessName());
        verify(consumptionRepository).saveAll(details);
    }

    private ConsumptionDetail consumptionDetail(String clientName) {
        return new ConsumptionDetail(
                null,
                2026,
                10,
                "Perú",
                "CC-1",
                clientName,
                "Servicio",
                "Comercial",
                "Entrada",
                "SMS",
                "Marca",
                "Proveedor",
                10,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                DataSource.DIRECT_DB);
    }
}
