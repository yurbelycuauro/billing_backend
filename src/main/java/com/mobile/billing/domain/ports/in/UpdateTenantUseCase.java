package com.mobile.billing.domain.ports.in;

import java.math.BigDecimal;

import com.mobile.billing.domain.model.Tenant;

public interface UpdateTenantUseCase {

    Tenant execute(UpdateTenantCommand command);

    record UpdateTenantCommand(
            Long id,
            String companyName,
            String taxId,
            BigDecimal taxRate,
            String currencyCode,
            String address,
            String phone,
            boolean active,
            String bankAccount) {
        public UpdateTenantCommand {
            if (id == null) {
                throw new IllegalArgumentException("El ID del Tenant es obligatorio.");
            }
        }
    }

}
