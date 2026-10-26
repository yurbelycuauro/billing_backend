package com.mobile.billing.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mobile.billing.domain.validator.ChileanRutValidator;
import com.mobile.billing.domain.validator.PeruvianRucValidator;
import com.mobile.billing.domain.validator.TaxIdValidator;
import com.mobile.billing.domain.validator.TaxIdValidatorResolver;

@Configuration
public class TaxIdValidatorConfiguration {

    @Bean
    public TaxIdValidator chileanRutValidator() {
        return new ChileanRutValidator();
    }

    @Bean
    public TaxIdValidator peruvianRucValidator() {
        return new PeruvianRucValidator();
    }

    @Bean
    public TaxIdValidatorResolver taxIdValidatorResolver(List<TaxIdValidator> validators) {
        return new TaxIdValidatorResolver(validators);
    }
}
