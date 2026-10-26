package com.mobile.billing.infrastructure.adapter.in.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.billing.domain.ports.in.CreatePaymentUseCase;
import com.mobile.billing.domain.ports.in.GetPaymentUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.PaymentRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.PaymentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final GetPaymentUseCase getPaymentUseCase;
    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentController(
            GetPaymentUseCase getPaymentUseCase,
            CreatePaymentUseCase createPaymentUseCase) {
        this.getPaymentUseCase = getPaymentUseCase;
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = getPaymentUseCase.getAllPayments().stream()
                .map(PaymentResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return getPaymentUseCase.getPaymentById(paymentId)
                .map(PaymentResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<PaymentResponse> getPaymentByMethod(@RequestParam String paymentMethod) {
        return getPaymentUseCase.getPaymentByMethod(paymentMethod)
                .map(PaymentResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByTenantId(@PathVariable Long tenantId) {
        List<PaymentResponse> payments = getPaymentUseCase.getPaymentsByTenantId(tenantId).stream()
                .map(PaymentResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        var command = new CreatePaymentUseCase.CreatePaymentCommand(request.paymentMethod(), request.termsDays());
        PaymentResponse response = PaymentResponse.fromDomain(createPaymentUseCase.createPayment(command));

        return ResponseEntity.created(URI.create("/api/v1/payments/" + response.id()))
                .body(response);
    }
}
