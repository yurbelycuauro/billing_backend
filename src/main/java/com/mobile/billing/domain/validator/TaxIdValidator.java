package com.mobile.billing.domain.validator;

public interface TaxIdValidator {

    String countryCode();

    boolean isValid(String taxId);

}
