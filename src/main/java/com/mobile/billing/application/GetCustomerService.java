package com.mobile.billing.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCustomerService implements GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Optional<Customer> getCustomerByBusinessName(String businessName) {
        return customerRepository.findByBusinessName(businessName);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
