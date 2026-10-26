package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.ports.in.GetTenantCurrenciesUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.TenantCurrencyResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final GetTenantCurrenciesUseCase getTenantCurrencies;

    public CurrencyController(GetTenantCurrenciesUseCase getTenantCurrencies) {
        this.getTenantCurrencies = getTenantCurrencies;
    }

    @GetMapping
    public ResponseEntity<List<TenantCurrencyResponse>> getAllowedCurrencies() {
        return ResponseEntity.ok(getTenantCurrencies.getAllowedCurrencies().stream()
                .map(TenantCurrencyResponse::fromDomain)
                .toList());
    }

}
