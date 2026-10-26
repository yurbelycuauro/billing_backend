package com.mobile.billing.domain.exception;

public class DuplicateCustomerTaxIdException extends RuntimeException {

    public DuplicateCustomerTaxIdException(String taxId) {
        super("Ya existe un customer con el taxId: " + taxId + ".");
    }
}
