package com.mobile.billing.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.Customer;

public interface GetCustomerUseCase {
    Optional<Customer> getCustomerById(Long customerId);
    Optional<Customer> getCustomerByBusinessName(String businessName);
    List<Customer> getAllCustomers();
    

}
