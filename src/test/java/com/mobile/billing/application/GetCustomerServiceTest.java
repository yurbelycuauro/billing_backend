package com.mobile.billing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;

class GetCustomerServiceTest {

    @Test
    void shouldReturnCustomerById() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        GetCustomerService service = new GetCustomerService(repository);

        Customer customer = Customer.create("12345678", "Acme S.A.", "contacto@acme.com");
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = service.getCustomerById(1L);

        assertTrue(result.isPresent());
        assertEquals("Acme S.A.", result.get().getBusinessName());
    }

    @Test
    void shouldReturnAllCustomers() {
        CustomerRepositoryPort repository = mock(CustomerRepositoryPort.class);
        GetCustomerService service = new GetCustomerService(repository);

        Customer customer = Customer.create("87654321", "Beta S.A.", "info@beta.com");
        when(repository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = service.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Beta S.A.", result.get(0).getBusinessName());
    }
}
