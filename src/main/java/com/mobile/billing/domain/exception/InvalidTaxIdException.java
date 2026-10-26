package com.mobile.billing.domain.exception;

public class InvalidTaxIdException extends RuntimeException {

    public InvalidTaxIdException(String countryCode) {
        super("El taxId no es válido para el país del tenant: " + countryCode + ".");
    }
}
