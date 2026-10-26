ALTER TABLE invoices
    DROP CHECK invoices_chk_1;

ALTER TABLE invoices
    MODIFY COLUMN moneda VARCHAR(3) NOT NULL;

UPDATE invoices
SET moneda = CASE moneda
    WHEN '0' THEN 'PEN'
    WHEN '1' THEN 'CLP'
    WHEN '2' THEN 'USD'
    WHEN '3' THEN 'EUR'
    ELSE moneda
END;
