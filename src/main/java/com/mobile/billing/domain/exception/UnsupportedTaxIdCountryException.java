package com.mobile.billing.domain.exception;

public class UnsupportedTaxIdCountryException extends RuntimeException {

    public UnsupportedTaxIdCountryException(String countryCode) {
        super("No hay una estrategia de validación de taxId para el país: " + countryCode + ".");
    }
}
