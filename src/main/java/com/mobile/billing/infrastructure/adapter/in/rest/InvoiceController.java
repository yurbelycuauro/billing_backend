package com.mobile.billing.infrastructure.adapter.in.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand;

import com.mobile.billing.infrastructure.adapter.in.rest.dto.GenerateInvoicePdfRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceRequest;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final GenerateInvoicePdfUseCase generateInvoicePdfUseCase;
    private final CreateInvoiceUseCase createInvoiceUseCase;
    

        @PostMapping(value = { "/pdf", "/generate" }, produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generateInvoicePdf(@Valid @RequestBody GenerateInvoicePdfRequest request) {

        GenerateInvoicePdfCommand command = new GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand(
                request.clientId(), request.anioPeriodo(), request.mesPeriodo(), request.paymentId(), request.moneda(),
                request.pais(), request.items());

        byte[] pdfContent = generateInvoicePdfUseCase.generateInvoicePdf(command);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice.pdf");

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body(pdfContent);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        CreateInvoiceUseCase.GenerateInvoiceCommand command = new CreateInvoiceUseCase.GenerateInvoiceCommand(
                request.clientId(), request.anioPeriodo(), request.mesPeriodo(), request.paymentId(), request.moneda(),
                request.pais(), request.items());

        Invoice invoiceResponse = createInvoiceUseCase.generateInvoice(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceResponse);

    }

    
}