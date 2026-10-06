package com.mobile.billing.domain.ports.in;

public interface CreateCustomerUseCase {

    record CreateCustomerCommand(
            String businessName,
            String notificationEmail,

            String taxId) {
    }

    Long createCustomer(CreateCustomerCommand command);

}
