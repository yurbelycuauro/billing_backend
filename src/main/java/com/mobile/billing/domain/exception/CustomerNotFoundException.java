package com.mobile.billing.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer no encontrado: " + id + ".");
    }
}
