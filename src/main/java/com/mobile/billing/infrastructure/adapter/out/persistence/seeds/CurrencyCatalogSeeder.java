package com.mobile.billing.infrastructure.adapter.out.persistence.seeds;

import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.infrastructure.adapter.out.persistence.SpringDataCurrencyRepository;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CurrencyEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2)
public class CurrencyCatalogSeeder implements CommandLineRunner {

    private final SpringDataCurrencyRepository currencies;

    public CurrencyCatalogSeeder(SpringDataCurrencyRepository currencies) {
        this.currencies = currencies;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (Moneda currency : Moneda.values()) {
            String code = currency.name();
            seedCurrency(code, currency.getNombre(), currency.getSimbolo(), currency.getDecimales());
        }
        seedCurrency("CLF", "Unidad de Fomento", "UF", 4);
    }

    private void seedCurrency(String code, String name, String symbol, int decimalDigits) {
        if (!currencies.existsById(code)) {
            currencies.save(new CurrencyEntity(code, name, symbol, decimalDigits, true));
        }
    }
}
