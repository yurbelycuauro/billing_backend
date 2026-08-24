package com.mobile.billing.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.Customer;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<String> findNameById(Long id);
    Optional<Customer> findByTaxId(String taxId);
    Optional<Customer> findByBusinessName(String businessName);
    List<Customer> findAll();
}
