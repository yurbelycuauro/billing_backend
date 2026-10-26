package com.mobile.billing.infrastructure.adapter.in.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase.CreateCustomerCommand;
import com.mobile.billing.domain.ports.in.GetCustomerUseCase;
import com.mobile.billing.domain.ports.in.UpdateCustomerUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.CreateCustomerDto;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.UpdateCustomerDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final GetCustomerUseCase getCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    public CustomerController(
            GetCustomerUseCase getCustomerUseCase,
            CreateCustomerUseCase createCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase) {
        this.getCustomerUseCase = getCustomerUseCase;
        this.createCustomerUseCase = createCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<Long> createCustomer(@Valid @RequestBody CreateCustomerDto createCustomerDto) {
        CreateCustomerCommand command = new CreateCustomerCommand(
                createCustomerDto.businessName(),
                createCustomerDto.notificationEmail(),
                createCustomerDto.taxId());
        Long customerId = createCustomerUseCase.createCustomer(command);
        return ResponseEntity.ok(customerId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerDto updateCustomerDto) {
        return ResponseEntity.ok(updateCustomerUseCase.updateCustomer(id, updateCustomerDto.toCommand()));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(getCustomerUseCase.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return getCustomerUseCase.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> getCustomerByBusinessName(@RequestParam String businessName) {
        return getCustomerUseCase.getCustomerByBusinessName(businessName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
