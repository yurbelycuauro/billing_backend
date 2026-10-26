package com.mobile.billing.domain.validator;

public class PeruvianRucValidator implements TaxIdValidator {

    @Override
    public String countryCode() {
        return "PE";
    }

    @Override
    public boolean isValid(String taxId) {
        if (taxId == null || !taxId.trim().matches("^\\d{11}$")) {
            return false;
        }
        taxId = taxId.trim();

        // Los RUCs válidos en Perú generalmente empiezan en 10, 15, 17 o 20
        String prefix = taxId.substring(0, 2);
        if (!prefix.equals("10") && !prefix.equals("15") && !prefix.equals("17") && !prefix.equals("20")) {
            return false;
        }

        // Validación matemática de los factores (Módulo 11 modificado para Perú)
        int[] factors = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(taxId.charAt(i)) * factors[i];
        }

        int remainder = sum % 11;
        int result = 11 - remainder;

        if (result == 10) {
            result = 0;
        } else if (result == 11) {
            result = 1;
        }

        int expectedDv = Character.getNumericValue(taxId.charAt(10));
        return result == expectedDv;
    }
}
