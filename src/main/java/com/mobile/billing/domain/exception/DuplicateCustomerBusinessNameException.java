package com.mobile.billing.domain.exception;

public class DuplicateCustomerBusinessNameException extends RuntimeException {

    public DuplicateCustomerBusinessNameException(String businessName, Long customerId) {
        super("Ya existe un customer con el businessName: " + businessName
                + " (ID: " + customerId + ").");
    }
}
