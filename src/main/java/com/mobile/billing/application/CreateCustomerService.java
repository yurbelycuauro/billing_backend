package com.mobile.billing.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final CustomerTaxIdValidationService taxIdValidationService;
    private final TenantContextPort tenantContext;

    public CreateCustomerService(
            CustomerRepositoryPort customerRepositoryPort,
            CustomerTaxIdValidationService taxIdValidationService,
            TenantContextPort tenantContext) {
        this.customerRepositoryPort = customerRepositoryPort;
        this.taxIdValidationService = taxIdValidationService;
        this.tenantContext = tenantContext;
    }

    @Override
    public Long createCustomer(CreateCustomerCommand command) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No hay un tenant activo en el contexto.");
        }

        String businessName = command.businessName();
        String taxId = command.taxId() == null ? null : command.taxId().trim();

        taxIdValidationService.validate(taxId);

        if (businessName != null && !businessName.isBlank()) {
            Optional<Customer> existingBusinessName = customerRepositoryPort
                    .findByTenantIdAndBusinessName(tenantId, businessName);
            if (existingBusinessName.isPresent()) {
                Customer customer = existingBusinessName.get();
                log.debug("businessName ya existe para customer id: {} ,tenantId: {}", customer.getId(), tenantId);
                throw new DuplicateCustomerBusinessNameException(customer.getBusinessName(), customer.getId());
            }
        }

        if (taxId != null && !taxId.isBlank()) {
            Optional<Customer> existingCustomer = customerRepositoryPort.findByTaxId(taxId);
            if (existingCustomer.isPresent()) {
                log.debug("existe customer id: {} ,tenantId: {}", existingCustomer.get().getId(), tenantId);
                return existingCustomer.get().getId();
            }
        }

        Customer customer = Customer.create(tenantId, taxId, businessName, command.notificationEmail());
        Customer savedCustomer = customerRepositoryPort.save(customer);

        log.debug("se creo customer  con ID: {}", savedCustomer.getId());
        return savedCustomer.getId();
    }

}
