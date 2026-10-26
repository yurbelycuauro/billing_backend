package com.mobile.billing.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.exception.CustomerNotFoundException;
import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.domain.exception.DuplicateCustomerTaxIdException;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.UpdateCustomerUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;

@Service
public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerTaxIdValidationService taxIdValidationService;
    private final TenantContextPort tenantContext;

    public UpdateCustomerService(
            CustomerRepositoryPort customerRepository,
            CustomerTaxIdValidationService taxIdValidationService,
            TenantContextPort tenantContext) {
        this.customerRepository = customerRepository;
        this.taxIdValidationService = taxIdValidationService;
        this.tenantContext = tenantContext;
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerCommand command) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        String taxId = command.taxId() == null ? null : command.taxId().trim();
        taxIdValidationService.validate(taxId);

        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No hay un tenant activo en el contexto.");
        }

        if (taxId != null) {
            customerRepository.findByTaxId(taxId)
                    .filter(existingCustomer -> !existingCustomer.getId().equals(id))
                    .ifPresent(existingCustomer -> {
                        throw new DuplicateCustomerTaxIdException(taxId);
                    });
        }

        customerRepository.findByTenantIdAndBusinessName(tenantId, command.businessName())
                .filter(existingCustomer -> !existingCustomer.getId().equals(id))
                .ifPresent(existingCustomer -> {
                    throw new DuplicateCustomerBusinessNameException(
                            existingCustomer.getBusinessName(), existingCustomer.getId());
                });

        customer.update(taxId, command.businessName(), command.notificationEmail());
        if (Boolean.TRUE.equals(command.active())) {
            customer.activate();
        } else if (Boolean.FALSE.equals(command.active())) {
            customer.deactivate();
        }
        return customerRepository.save(customer);
    }
}
