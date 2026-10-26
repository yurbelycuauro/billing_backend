package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.Customer;

public interface UpdateCustomerUseCase {

    Customer updateCustomer(Long id, UpdateCustomerCommand command);

    record UpdateCustomerCommand(
            String businessName,
            String notificationEmail,
            String taxId,
            Boolean active) {
    }
}
