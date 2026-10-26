package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.ports.in.DisableTenantCurrencyUseCase;
import com.mobile.billing.domain.ports.in.EnableTenantCurrencyUseCase;
import com.mobile.billing.domain.ports.in.GetTenantCurrenciesUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.EnableTenantCurrencyRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.TenantCurrencyResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import com.mobile.billing.domain.model.TenantCurrency;

@RestController
@RequestMapping("/api/v1/tenant-currencies")
public class TenantCurrencyController {

    private final EnableTenantCurrencyUseCase enableCurrency;
    private final DisableTenantCurrencyUseCase disableCurrency;
    private final GetTenantCurrenciesUseCase getTenantCurrencies;

    public TenantCurrencyController(
            EnableTenantCurrencyUseCase enableCurrency,
            DisableTenantCurrencyUseCase disableCurrency,
            GetTenantCurrenciesUseCase getTenantCurrencies) {
        this.enableCurrency = enableCurrency;
        this.disableCurrency = disableCurrency;
        this.getTenantCurrencies = getTenantCurrencies;
    }

    @GetMapping
    public ResponseEntity<List<TenantCurrencyResponse>> getTenantCurrencies() {
        List<TenantCurrency> tenantCurrencies = getTenantCurrencies.getAllowedCurrencies();

        List<TenantCurrencyResponse> responses = tenantCurrencies.stream()
                .map(TenantCurrencyResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{currencyCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantCurrencyResponse> enableCurrency(
            @PathVariable String currencyCode,
            @Valid @RequestBody EnableTenantCurrencyRequest request) {
        var tenantCurrency = enableCurrency.enableCurrency(currencyCode, request.isDefault());
        return ResponseEntity.ok(TenantCurrencyResponse.fromDomain(tenantCurrency));
    }

    @DeleteMapping("/{currencyCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableCurrency(@PathVariable String currencyCode) {
        disableCurrency.disableCurrency(currencyCode);
        return ResponseEntity.noContent().build();
    }
}
