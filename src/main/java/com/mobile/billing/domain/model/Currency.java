package com.mobile.billing.domain.model;

public record Currency(CurrencyCode code, String name, String symbol, int decimalDigits, boolean active) {

    public Currency {
        if (code == null) {
            throw new IllegalArgumentException("Currency code is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Currency name is required.");
        }
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Currency symbol is required.");
        }
        if (decimalDigits < 0 || decimalDigits > 4) {
            throw new IllegalArgumentException("Currency decimal digits must be between 0 and 4.");
        }
    }
}
