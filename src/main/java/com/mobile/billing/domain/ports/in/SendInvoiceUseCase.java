package com.mobile.billing.domain.ports.in;

public interface SendInvoiceUseCase {

    void execute(SendInvoiceCommand command);

    record SendInvoiceCommand(String email, byte[] pdfContent, String fileName) {
    }

}
