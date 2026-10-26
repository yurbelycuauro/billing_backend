package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.exception.InvalidTaxIdException;
import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.domain.validator.ChileanRutValidator;
import com.mobile.billing.domain.validator.PeruvianRucValidator;
import com.mobile.billing.domain.validator.TaxIdValidatorResolver;

class UpdateCustomerServiceTest {

    @Test
    void shouldUpdateCustomerUsingPeruvianTenantStrategy() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        Customer existingCustomer = new Customer(5L, "20100070970", "Old Name", "old@example.com", true);
        when(repository.findById(5L)).thenReturn(Optional.of(existingCustomer));
        when(repository.findByTaxId("20100070970")).thenReturn(Optional.of(existingCustomer));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCustomerService service = service(repository, "PE");
        Customer updated = service.updateCustomer(
                5L,
                new UpdateCustomerCommand("New Name", "new@example.com", "20100070970", false));

        assertEquals("New Name", updated.getBusinessName());
        assertEquals("new@example.com", updated.getNotificationEmail());
        assertEquals("20100070970", updated.getTaxId());
        assertFalse(updated.isActive());
    }

    @Test
    void shouldRejectInvalidTaxIdForTenantCountryWithoutSaving() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        when(repository.findById(5L)).thenReturn(
                Optional.of(new Customer(5L, "11111111-1", "Old Name", "old@example.com", true)));

        UpdateCustomerService service = service(repository, "PE");

        assertThrows(InvalidTaxIdException.class, () -> service.updateCustomer(
                5L,
                new UpdateCustomerCommand("New Name", "new@example.com", "11111111-1", null)));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void shouldKeepActiveStatusWhenUpdateDoesNotSpecifyIt() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        Customer existingCustomer = new Customer(5L, "20100070970", "Old Name", "old@example.com", true);
        when(repository.findById(5L)).thenReturn(Optional.of(existingCustomer));
        when(repository.findByTaxId("20100070970")).thenReturn(Optional.of(existingCustomer));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = service(repository, "PE").updateCustomer(
                5L,
                new UpdateCustomerCommand("New Name", "new@example.com", "20100070970", null));

        assertTrue(updated.isActive());
    }

    @Test
    void shouldRejectBusinessNameAlreadyUsedByAnotherCustomerInSameTenant() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        Customer customer = new Customer(5L, 1L, "20100070970", "Old Name", "old@example.com", true);
        Customer duplicate = new Customer(8L, 1L, "20100070971", "Existing Name", "existing@example.com", true);
        when(repository.findById(5L)).thenReturn(Optional.of(customer));
        when(repository.findByTenantIdAndBusinessName(1L, "Existing Name")).thenReturn(Optional.of(duplicate));

        assertThrows(DuplicateCustomerBusinessNameException.class, () -> service(repository, "PE").updateCustomer(
                5L,
                new UpdateCustomerCommand("Existing Name", "new@example.com", "20100070970", null)));
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void shouldAllowKeepingBusinessNameWhenUpdatingSameCustomer() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        Customer customer = new Customer(5L, 1L, "20100070970", "Same Name", "old@example.com", true);
        when(repository.findById(5L)).thenReturn(Optional.of(customer));
        when(repository.findByTaxId("20100070970")).thenReturn(Optional.of(customer));
        when(repository.findByTenantIdAndBusinessName(1L, "Same Name")).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = service(repository, "PE").updateCustomer(
                5L,
                new UpdateCustomerCommand("Same Name", "new@example.com", "20100070970", null));

        assertEquals("Same Name", updated.getBusinessName());
    }

    private UpdateCustomerService service(CustomerRepositoryPort repository, String countryCode) {
        TenantContextPort tenantContext = mock(TenantContextPort.class);
        TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
        when(tenantContext.getTenantId()).thenReturn(1L);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(
                new Tenant(1L, countryCode, "Tenant", "tax-id", BigDecimal.ZERO,
                        "PEN", "", "", true)));

        CustomerTaxIdValidationService validationService = new CustomerTaxIdValidationService(
                tenantContext,
                tenantRepository,
                new TaxIdValidatorResolver(List.of(new ChileanRutValidator(), new PeruvianRucValidator())));
        return new UpdateCustomerService(repository, validationService, tenantContext);
    }
}
