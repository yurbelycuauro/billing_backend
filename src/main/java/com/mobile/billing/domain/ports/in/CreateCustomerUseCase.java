package com.mobile.billing.domain.ports.in;

public interface CreateCustomerUseCase {

    record CreateCustomerCommand(
        String businessName,
        String pais,
        String notificationEmail,
        String ruc,
        String taxId
    ) {}
    
    Long createCustomer(CreateCustomerCommand command);

}
