package com.mobile.billing.application;

import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    @Override
    public Long createCustomer(CreateCustomerCommand command) {
        String businessName = command.businessName();
        String taxId = command.taxId();
        String normalizedBusinessName = normalizeBusinessName(businessName);

        Optional<Customer> existingCustomer = Optional.empty();
        if (taxId != null && !taxId.isBlank()) {
            existingCustomer = customerRepositoryPort.findByTaxId(taxId.trim());
        }

        if (existingCustomer.isEmpty() && businessName != null && !businessName.isBlank()) {
            existingCustomer = customerRepositoryPort.findByBusinessName(businessName.trim());
        }

        if (existingCustomer.isEmpty() && businessName != null && !businessName.isBlank()) {
            existingCustomer = customerRepositoryPort.findAll().stream()
                    .filter(customer -> normalizeBusinessName(customer.getBusinessName())
                            .equals(normalizedBusinessName))
                    .findFirst();
        }

        if (existingCustomer.isPresent()) {
            log.debug("existe customer id: {}", existingCustomer.get().getId());
            return existingCustomer.get().getId();
        }

        Customer customer = Customer.create(taxId, businessName, command.notificationEmail());
        Customer savedCustomer = customerRepositoryPort.save(customer);

        log.debug("se creo customer  con ID: {}", savedCustomer.getId());
        return savedCustomer.getId();
    }

    private String normalizeBusinessName(String businessName) {
        return businessName == null ? "" : businessName.trim().toLowerCase(Locale.ROOT);
    }

}
