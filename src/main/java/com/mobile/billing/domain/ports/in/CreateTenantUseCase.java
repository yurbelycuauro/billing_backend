package com.mobile.billing.domain.ports.in;

import java.math.BigDecimal;

public interface CreateTenantUseCase {

    record CreateTenantCommand(
        String countryCode,
        String companyName,
        String taxId,
        BigDecimal taxRate,
        String currencyCode,
        String address,
        String phone,
        boolean active,
        String bankAccount
    ) {}
    
    Long createTenant(CreateTenantCommand command);

}
