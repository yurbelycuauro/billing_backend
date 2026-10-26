package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.exception.InvalidTaxIdException;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase.CreateCustomerCommand;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.domain.validator.ChileanRutValidator;
import com.mobile.billing.domain.validator.PeruvianRucValidator;
import com.mobile.billing.domain.validator.TaxIdValidatorResolver;

class CreateCustomerServiceTest {

    @Test
    void shouldCreateCustomerWhenBusinessNameDoesNotExist() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = service(repository, "CL");

        when(repository.findByTenantIdAndBusinessName(1L, "Acme S.A.")).thenReturn(Optional.empty());
        Customer savedCustomer = new Customer(1L, "11111111-1", "Acme S.A.", "contacto@acme.com", true);
        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        Long id = service.createCustomer(
                new CreateCustomerCommand("Acme S.A.", "contacto@acme.com", "11111111-1"));

        assertEquals(1L, id);
        org.mockito.ArgumentCaptor<Customer> customerCaptor = org.mockito.ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(customerCaptor.capture());
        assertEquals(1L, customerCaptor.getValue().getTenantId());
    }

    @Test
    void shouldRejectCustomerWhenBusinessNameAlreadyExists() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = service(repository, "CL");

        Customer existingCustomer = new Customer(7L, "11111111-1", "Beta S.A.", "info@beta.com", true);
        when(repository.findByTenantIdAndBusinessName(1L, "Beta S.A.")).thenReturn(Optional.of(existingCustomer));

        DuplicateCustomerBusinessNameException exception = assertThrows(
                DuplicateCustomerBusinessNameException.class,
                () -> service.createCustomer(
                        new CreateCustomerCommand("Beta S.A.", "info@beta.com", "11111111-1")));

        assertEquals("Ya existe un customer con el businessName: Beta S.A. (ID: 7).", exception.getMessage());
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void shouldRejectCustomerWhenBusinessNameExistsWithDifferentCaseOrWhitespace() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = service(repository, "CL");
        Customer existingCustomer = new Customer(7L, "11111111-1", "Beta S.A.", "info@beta.com", true);

        when(repository.findByTenantIdAndBusinessName(1L, " beta s.a. ")).thenReturn(Optional.of(existingCustomer));

        assertThrows(
                DuplicateCustomerBusinessNameException.class,
                () -> service.createCustomer(
                        new CreateCustomerCommand(" beta s.a. ", "contacto@beta.com", "11111111-1")));

        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void shouldNotCreateCustomerWhenTaxIdAlreadyExists() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = service(repository, "CL");

        Customer existingCustomer = new Customer(11L, "11111111-1", "Alo S.A.", "contacto@alo.com", true);
        when(repository.findByTaxId("11111111-1")).thenReturn(Optional.of(existingCustomer));

        Long id = service
                .createCustomer(new CreateCustomerCommand("Alo S.A.", "contacto@alo.com", "11111111-1"));

        assertEquals(existingCustomer.getId(), id);
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void shouldRejectTaxIdThatDoesNotMatchTenantCountry() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = service(repository, "CL");

        assertThrows(
                InvalidTaxIdException.class,
                () -> service.createCustomer(
                        new CreateCustomerCommand("Acme S.A.", "contacto@acme.com", "20100070970")));
        verify(repository, never()).save(any(Customer.class));
    }

    private CreateCustomerService service(CustomerRepositoryPort repository, String countryCode) {
        TenantContextPort tenantContext = mock(TenantContextPort.class);
        TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
        when(tenantContext.getTenantId()).thenReturn(1L);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(
                new Tenant(1L, countryCode, "Tenant", "tax-id", BigDecimal.ZERO,
                        "CLP", "", "", true)));

        CustomerTaxIdValidationService validationService = new CustomerTaxIdValidationService(
                tenantContext,
                tenantRepository,
                new TaxIdValidatorResolver(List.of(new ChileanRutValidator(), new PeruvianRucValidator())));
        return new CreateCustomerService(repository, validationService, tenantContext);
    }
}
