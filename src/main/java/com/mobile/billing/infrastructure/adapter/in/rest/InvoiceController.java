package com.mobile.billing.infrastructure.adapter.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mobile.billing.domain.ports.in.CreateInvoiceUseCase;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase;
import com.mobile.billing.domain.ports.in.GenerateInvoicePdfUseCase.GenerateInvoicePdfCommand;
import com.mobile.billing.domain.ports.in.GetInvoiceUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.GenerateInvoicePdfRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.InvoiceResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

        private final GenerateInvoicePdfUseCase generateInvoicePdfUseCase;
        private final CreateInvoiceUseCase createInvoiceUseCase;
        private final GetInvoiceUseCase getInvoiceUseCase;

        public InvoiceController(
                        GenerateInvoicePdfUseCase generateInvoicePdfUseCase,
                        CreateInvoiceUseCase createInvoiceUseCase,
                        GetInvoiceUseCase getInvoiceUseCase) {
                this.generateInvoicePdfUseCase = generateInvoicePdfUseCase;
                this.createInvoiceUseCase = createInvoiceUseCase;
                this.getInvoiceUseCase = getInvoiceUseCase;
        }

        @GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long invoiceId) {
                var invoice = getInvoiceUseCase.getInvoiceById(invoiceId);
                return ResponseEntity.ok(InvoiceResponse.fromDomain(invoice));
        }

        @GetMapping(value = "/period", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<InvoiceResponse>> findInvoicesByPeriod(
                        @RequestParam Integer year,
                        @RequestParam Integer month,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

                Page<InvoiceResponse> response = getInvoiceUseCase
                                .findByPeriodYearAndPeriodMonth(year, month, pageable)
                                .map(InvoiceResponse::fromDomain);

                return ResponseEntity.ok(response);
        }

        @GetMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<InvoiceResponse>> findInvoicesByPeriodAndCustomer(
                        @RequestParam Integer year,
                        @RequestParam Integer month,
                        @RequestParam Long customerId,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

                Page<InvoiceResponse> response = getInvoiceUseCase
                                .findByPeriodYearAndPeriodMonthAndCustomerId(year, month, customerId, pageable)
                                .map(InvoiceResponse::fromDomain);

                return ResponseEntity.ok(response);
        }

        @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<InvoiceResponse>> findAllInvoices(
                        @RequestParam Integer year,
                        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

                Page<InvoiceResponse> response = getInvoiceUseCase
                                .findAll(year, pageable)
                                .map(InvoiceResponse::fromDomain);

                return ResponseEntity.ok(response);
        }

        @PostMapping(value = { "/pdf", "/generate" }, produces = MediaType.APPLICATION_PDF_VALUE)
        public ResponseEntity<byte[]> generateInvoicePdf(@Valid @RequestBody GenerateInvoicePdfRequest request) {
                GenerateInvoicePdfCommand command = new GenerateInvoicePdfCommand(
                                request.clientId(), request.anioPeriodo(), request.mesPeriodo(), request.paymentId(),
                                request.moneda(), request.items());

                byte[] pdfContent = generateInvoicePdfUseCase.generateInvoicePdf(command);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "invoice.pdf");

                return ResponseEntity.status(HttpStatus.CREATED)
                                .headers(headers)
                                .body(pdfContent);
        }

        @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
                CreateInvoiceUseCase.GenerateInvoiceCommand command = new CreateInvoiceUseCase.GenerateInvoiceCommand(
                                request.clientId(), request.anioPeriodo(), request.mesPeriodo(), request.paymentId(),
                                request.moneda(), request.items());

                var invoiceCreated = createInvoiceUseCase.generateInvoice(command);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(InvoiceResponse.fromDomain(invoiceCreated));
        }
}