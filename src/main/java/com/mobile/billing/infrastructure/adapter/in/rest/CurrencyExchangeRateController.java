package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.ports.in.CreateExchangeRateUseCase;
import com.mobile.billing.domain.ports.in.GetExchangeRateUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.CreateExchangeRateRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.ExchangeRateResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currency-exchange-rates")
public class CurrencyExchangeRateController {

    private final CreateExchangeRateUseCase createExchangeRate;
    private final GetExchangeRateUseCase getExchangeRate;

    public CurrencyExchangeRateController(
            CreateExchangeRateUseCase createExchangeRate,
            GetExchangeRateUseCase getExchangeRate) {
        this.createExchangeRate = createExchangeRate;
        this.getExchangeRate = getExchangeRate;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponse> createForCurrentTenant(
            @Valid @RequestBody CreateExchangeRateRequest request) {
        var rate = createExchangeRate.createForCurrentTenant(request.toCommand());
        ExchangeRateResponse response = ExchangeRateResponse.fromDomain(rate);
        return ResponseEntity.created(URI.create("/api/v1/currency-exchange-rates/" + response.id()))
                .body(response);
    }

    @PostMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExchangeRateResponse> createGlobal(
            @Valid @RequestBody CreateExchangeRateRequest request) {
        var rate = createExchangeRate.createGlobal(request.toCommand());
        ExchangeRateResponse response = ExchangeRateResponse.fromDomain(rate);
        return ResponseEntity.created(URI.create("/api/v1/currency-exchange-rates/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam LocalDate date) {
        var rate = getExchangeRate.getExchangeRate(from, to, date);
        return ResponseEntity.ok(ExchangeRateResponse.fromDomain(rate));
    }
}
