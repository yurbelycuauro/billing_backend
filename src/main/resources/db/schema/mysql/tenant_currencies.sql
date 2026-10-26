CREATE TABLE IF NOT EXISTS tenant_currencies (
    tenant_id BIGINT NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, currency_code),
    INDEX idx_tenant_currencies_tenant_id (tenant_id),
    CONSTRAINT fk_tenant_currencies_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant_entity(id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_currencies_currency
        FOREIGN KEY (currency_code) REFERENCES currencies(code) ON DELETE CASCADE
);

INSERT IGNORE INTO tenant_currencies (tenant_id, currency_code, is_default)
SELECT tenant.id, currency.code, TRUE
FROM tenant_entity tenant
JOIN currencies currency ON currency.code = tenant.currency_code AND currency.active = TRUE;
