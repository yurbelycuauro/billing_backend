package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase.CreateCustomerCommand;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;

class CreateCustomerServiceTest {

    @Test
    void shouldCreateCustomerWhenBusinessNameDoesNotExist() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = new CreateCustomerService(repository);

        when(repository.findByBusinessName("Acme S.A.")).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of());
        Customer savedCustomer = new Customer(1L, "12345678", "Acme S.A.", "contacto@acme.com", true);
        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        Long id = service.createCustomer(new CreateCustomerCommand("Acme S.A.", "Perú", "contacto@acme.com", "12345678", "12345678"));

        assertEquals(1L, id);
        verify(repository).save(any(Customer.class));
    }

    @Test
    void shouldNotCreateCustomerWhenBusinessNameAlreadyExists() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        CreateCustomerService service = new CreateCustomerService(repository);

        Customer existingCustomer = new Customer(7L, "87654321", "Beta S.A.", "info@beta.com", true);
        when(repository.findByBusinessName("Beta S.A.")).thenReturn(Optional.of(existingCustomer));

        Long id = service.createCustomer(new CreateCustomerCommand("Beta S.A.", "Perú", "info@beta.com", "87654321", "87654321"));

        assertEquals(existingCustomer.getId(), id);
        verify(repository, never()).save(any(Customer.class));
    }
}
