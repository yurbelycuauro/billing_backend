CREATE TABLE IF NOT EXISTS tenant_currencies (
    tenant_id BIGINT NOT NULL REFERENCES tenant_entity(id) ON DELETE CASCADE,
    currency_code VARCHAR(3) NOT NULL REFERENCES currencies(code) ON DELETE CASCADE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, currency_code)
);

CREATE INDEX IF NOT EXISTS idx_tenant_currencies_tenant_id
    ON tenant_currencies(tenant_id);

INSERT INTO tenant_currencies (tenant_id, currency_code, is_default)
SELECT tenant.id, currency.code, TRUE
FROM tenant_entity tenant
JOIN currencies currency ON currency.code = tenant.currency_code AND currency.active = TRUE
ON CONFLICT (tenant_id, currency_code) DO NOTHING;
