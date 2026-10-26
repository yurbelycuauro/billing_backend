package com.mobile.billing.domain.validator;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mobile.billing.domain.exception.UnsupportedTaxIdCountryException;

public class TaxIdValidatorResolver {

    private final Map<String, TaxIdValidator> validators;

    public TaxIdValidatorResolver(Iterable<TaxIdValidator> validators) {
        this.validators = StreamSupport.stream(validators.spliterator(), false)
                .collect(Collectors.toUnmodifiableMap(
                        validator -> validator.countryCode().trim().toUpperCase(Locale.ROOT),
                        Function.identity()));
    }

    public TaxIdValidator resolve(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new UnsupportedTaxIdCountryException(countryCode);
        }
        TaxIdValidator validator = validators.get(countryCode.trim().toUpperCase(Locale.ROOT));
        if (validator == null) {
            throw new UnsupportedTaxIdCountryException(countryCode);
        }
        return validator;
    }
}
