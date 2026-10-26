package com.mobile.billing.application;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.exception.InvalidTaxIdException;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;
import com.mobile.billing.domain.validator.TaxIdValidatorResolver;

@Service
public class CustomerTaxIdValidationService {

    private final TenantContextPort tenantContext;
    private final TenantRepositoryPort tenantRepository;
    private final TaxIdValidatorResolver validatorResolver;

    public CustomerTaxIdValidationService(
            TenantContextPort tenantContext,
            TenantRepositoryPort tenantRepository,
            TaxIdValidatorResolver validatorResolver) {
        this.tenantContext = tenantContext;
        this.tenantRepository = tenantRepository;
        this.validatorResolver = validatorResolver;
    }

    public void validate(String taxId) {
        if (taxId == null) {
            return;
        }

        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No hay un tenant activo en el contexto.");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("El tenant activo no existe."));
        String countryCode = tenant.getCountryCode();
        if (!validatorResolver.resolve(countryCode).isValid(taxId)) {
            throw new InvalidTaxIdException(countryCode);
        }
    }
}
