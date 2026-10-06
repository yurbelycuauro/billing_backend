package com.mobile.billing.infrastructure.adapter.in.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.billing.domain.ports.in.CreateTenantUseCase;
import com.mobile.billing.domain.ports.in.DeactivateTenantUseCase;
import com.mobile.billing.domain.ports.in.GetTenantUseCase;
import com.mobile.billing.domain.ports.in.UpdateTenantUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.CreateTenantRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.TenantResponse;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.UpdateTenantRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetTenantUseCase getTenantUseCase;
    private final UpdateTenantUseCase updateTenantUseCase;
    private final DeactivateTenantUseCase deactivateTenantUseCase;

    @PostMapping
    public ResponseEntity<Long> createTenant(@RequestBody CreateTenantRequest request) {
        Long tenantId = createTenantUseCase.createTenant(request.toCommand());
        return ResponseEntity.created(URI.create("/api/v1/tenants/" + tenantId)).body(tenantId);
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = getTenantUseCase.getAllTenants().stream()
                .map(TenantResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable Long id) {
        return getTenantUseCase.getTenantById(id)
                .map(TenantResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable Long id,
            @RequestBody UpdateTenantRequest request) {
        if (getTenantUseCase.getTenantById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TenantResponse response = TenantResponse.fromDomain(
                updateTenantUseCase.execute(request.toCommand(id)));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTenant(@PathVariable Long id) {
        if (getTenantUseCase.getTenantById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        deactivateTenantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}