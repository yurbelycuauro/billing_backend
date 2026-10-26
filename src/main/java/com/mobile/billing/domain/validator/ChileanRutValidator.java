package com.mobile.billing.domain.validator;

public class ChileanRutValidator implements TaxIdValidator {

    @Override
    public String countryCode() {
        return "CL";
    }

    @Override
    public boolean isValid(String taxId) {
        if (taxId == null || taxId.isBlank()) {
            return false;
        }

        String cleanRut = taxId.trim().toUpperCase().replace(".", "").replace("-", "");

        if (!cleanRut.matches("^\\d{7,8}[0-9K]$")) {
            return false;
        }

        try {
            int body = Integer.parseInt(cleanRut.substring(0, cleanRut.length() - 1));
            char expectedDv = cleanRut.charAt(cleanRut.length() - 1);

            return calculateChileanDv(body) == expectedDv;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private char calculateChileanDv(int body) {
        int sum = 0;
        int multiplier = 2;

        while (body > 0) {
            sum += (body % 10) * multiplier;
            body /= 10;
            multiplier = (multiplier == 7) ? 2 : multiplier + 1;
        }

        int remainder = 11 - (sum % 11);
        if (remainder == 11) {
            return '0';
        }
        if (remainder == 10) {
            return 'K';
        }
        return (char) (remainder + '0');
    }
}
