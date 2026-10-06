package com.mobile.billing.config.tenant;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateTenantConfiguration {

    @Bean
    public HibernatePropertiesCustomizer tenantIdentifierResolver() {
        return properties -> properties.put(
                AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
            new HibernateTenantIdentifierResolver());
    }
}